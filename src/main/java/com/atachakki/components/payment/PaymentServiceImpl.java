package com.atachakki.components.payment;

import com.atachakki.components.customer.Customer;
import com.atachakki.components.customer.CustomerRepository;
import com.atachakki.components.customerLedger.*;
import com.atachakki.components.operation.ShopOperationService;
import com.atachakki.components.order.Order;
import com.atachakki.components.order.OrderRepository;
import com.atachakki.components.staff.ShopStaff;
import com.atachakki.entity.User;
import com.atachakki.entity.type.Module;
import com.atachakki.entity.type.PaymentStatus;
import com.atachakki.exception.businessLogic.BusinessLogicException;
import com.atachakki.exception.businessLogic.CustomerIsBlockedException;
import com.atachakki.exception.entityNotFound.*;
import com.atachakki.exception.validation.PaymentValidationFailed;
import com.atachakki.repository.ShopStaffRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShopStaffRepository shopStaffRepository;
    private final CustomerRepository customerRepository;
    private final ShopOperationService shopOperationService;
    private final OrderRepository orderRepository;
    private final CustomerLedgerRepository customerLedgerRepository;
    private final CustomerLedgerMapper customerLedgerMapper;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            ShopStaffRepository shopStaffRepository,
            CustomerRepository customerRepository,
            ShopOperationService shopOperationService,
            OrderRepository orderRepository,
            CustomerLedgerRepository customerLedgerRepository,
            CustomerLedgerMapper customerLedgerMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.shopStaffRepository = shopStaffRepository;
        this.customerRepository = customerRepository;
        this.shopOperationService = shopOperationService;
        this.orderRepository = orderRepository;
        this.customerLedgerRepository = customerLedgerRepository;
        this.customerLedgerMapper = customerLedgerMapper;
    }

    @Override
    public Page<PaymentResponseDto> findPayments(
            Long shopId, Long customerId, Integer page,
            Integer size, String direction, String sort
    ) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<Payment> paymentsPage = paymentRepository
                .findByDeletedFalseAndCustomerShopIdAndCustomerId(
                        shopId, customerId, PageRequest.of(page, size, dir, sort)
                );
        return paymentsPage.map(paymentMapper::toResponseDto);
    }

    @Override
    public PaymentResponseDto findPayment(Long shopId, Long paymentId) {
        Payment payment = fetchPayment(paymentId, shopId);
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    public List<Long> findPaymentIds(Long shopId, Long customerId) {
        return paymentRepository.findIdsByCustomerId(customerId);
    }

    @Override
    @Transactional
    public PaymentResponseDto create(Long shopId, Long customerId, PaymentBundleRequestDto requestDto) {

        Page<Order> orderPage = validateFetchOrders(shopId, customerId, requestDto.getOrderIds());

        PaymentRequestDto pay = requestDto.getPaymentRequestDto();

        CustomerLedgerRequestDto customerLedgerRequestDto = requestDto.getDueOrRefundRequestDto();
        validatePayment(pay, orderPage, customerLedgerRequestDto);

        ShopStaff staff = getCurrentStaff(shopId);
        Customer customer = fetchCustomer(customerId, shopId);
        if (customer.getBlock()) {
            log.warn("Customer is blocked");
            throw new CustomerIsBlockedException(customerId);
        }

        Payment entity = paymentMapper.toEntity(pay);
        entity.setReceiver(staff);
        entity.setCustomer(customer);
        Payment newPayment = paymentRepository.save(entity);

        // update orders payment status
        orderPage.forEach(o -> o.setPaymentStatus(PaymentStatus.PAID));
        orderRepository.saveAll(orderPage.getContent());

        if (customerLedgerRequestDto != null) {
            CustomerLedger customerLedger = customerLedgerMapper.toEntity(customerLedgerRequestDto);
            customerLedger.setAddedBy(staff);
            customerLedgerRepository.save(customerLedger);
        }

        PaymentResponseDto responseDto = paymentMapper.toResponseDto(newPayment);
        shopOperationService.createModule(shopId, newPayment.getReceiver().getId(),
                Module.PAYMENT, responseDto.id(), responseDto.toString());
        return responseDto;
    }

    @Transactional
    public PaymentResponseDto payOrders(Long shopId, Long customerId, PaymentBundleRequestDto bundle) {

        ShopStaff staff = getCurrentStaff(shopId);
        Customer customer = fetchCustomer(customerId, shopId);

        // 1. fetch bundle
        List<Long> orderIds = bundle.getOrderIds();
        PaymentRequestDto paymentRequest = bundle.getPaymentRequestDto();
        CustomerLedgerRequestDto clRequest = bundle.getDueOrRefundRequestDto();
        CustomerLedgerRequestDto finalCLRequest = bundle.getFinalCustomerLedgerRequestDto();

        // 2. fetch order by Ids
        Page<Order> orderPage = getOrderPage(orderIds, customerId, shopId);

        BigDecimal totalOrderAmount = orderPage.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean isDueReqCL = clRequest.type().name().equalsIgnoreCase(CustomerLedgerType.DUE.name());
        BigDecimal payAmtSumTOdrAmt = isDueReqCL
                ? paymentRequest.amount().add(clRequest.amount())
                : paymentRequest.amount().subtract(clRequest.amount());
        // must match total amount of orders from request and db order's amount sum
        if (payAmtSumTOdrAmt.compareTo(totalOrderAmount) != 0) {
            log.debug("Order Total Amt != payAmt + newDueAmt");
            throw new BusinessLogicException("Order Total Amt != payAmt + newDueAmt", "Check due pay calculation");
        }

        CustomerLedger customerLedger = getCustomerLedger(shopId, customerId);
        boolean isDueDbCL = customerLedger.getType().name().equalsIgnoreCase(CustomerLedgerType.DUE.name());

        // DUE Request
        if (isDueReqCL) {
            if (totalOrderAmount.compareTo(paymentRequest.amount().add(clRequest.amount())) != 0) {
                log.debug("TotalAmount - DueAmount != payAmount");
                throw new BusinessLogicException("Calculation error", "TotalAmount - DueAmount != payAmount");
            }

            if (isDueDbCL) {
                BigDecimal finalDue = customerLedger.getAmount().add(clRequest.amount());
                if (finalDue.compareTo(finalCLRequest.amount()) != 0) {
                    log.debug("oldDue + newDue != finalDue");
                    throw new BusinessLogicException("Order Total Amt != payAmt + newDueAmt", "Check due pay calculation");
                }
                customerLedger.setAmount(finalDue);
                customerLedger.setType(CustomerLedgerType.DUE);
            } else {
                BigDecimal finalAmount;
                CustomerLedgerType finalType;

                // here db customerLedger is Refund
                int compare = customerLedger.getAmount().compareTo(clRequest.amount());

                if (compare > 0) {
                    // Overpayment → becomes REFUND
                    finalAmount = customerLedger.getAmount().subtract(clRequest.amount());
                    finalType = CustomerLedgerType.REFUND;
                } else if (compare < 0) {
                    // Partial payment → still DUE
                    finalAmount = clRequest.amount().subtract(customerLedger.getAmount());
                    finalType = CustomerLedgerType.DUE;

                } else {
                    // Exact payment → zero
                    finalAmount = BigDecimal.ZERO;
                    finalType = CustomerLedgerType.DUE; // or keep previous
                }

                if (finalAmount.compareTo(finalCLRequest.amount()) != 0 ||
                        !finalType.name().equalsIgnoreCase(finalCLRequest.type().name())) {
                    log.debug("oldDue + newDue != finalDue");
                    throw new BusinessLogicException("Order Total Amt != payAmt + newDueAmt", "Check due pay calculation");
                }
                customerLedger.setAmount(finalAmount);
                customerLedger.setType(finalType);
            }
        }
        // Refund Request
        else {
            if (totalOrderAmount.compareTo(paymentRequest.amount().subtract(clRequest.amount())) != 0) {
                log.debug("TotalAmount + DueAmount != payAmount");
                throw new BusinessLogicException("Calculation error", "TotalAmount + DueAmount != payAmount");
            }

            if (isDueDbCL) {
                BigDecimal finalAmount;
                CustomerLedgerType finalType;

                // here db customerLedger is Due
                int compare = customerLedger.getAmount().compareTo(clRequest.amount());

                if (compare < 0) {
                    // Overpayment → becomes REFUND
                    finalAmount = clRequest.amount().subtract(customerLedger.getAmount());
                    finalType = CustomerLedgerType.REFUND;
                } else if (compare > 0) {
                    // Partial payment → still DUE
                    finalAmount = customerLedger.getAmount().subtract(clRequest.amount());
                    finalType = CustomerLedgerType.DUE;

                } else {
                    // Exact payment → zero
                    finalAmount = BigDecimal.ZERO;
                    finalType = CustomerLedgerType.DUE; // or keep previous
                }

                if (finalAmount.compareTo(finalCLRequest.amount()) != 0 ||
                        !finalType.name().equalsIgnoreCase(finalCLRequest.type().name())) {
                    log.debug("oldDue + newDue != finalDue");
                    throw new BusinessLogicException("Order Total Amt != payAmt + newDueAmt", "Check due pay calculation");
                }
                customerLedger.setAmount(finalAmount);
                customerLedger.setType(finalType);
            } else {
                BigDecimal finalRefund = customerLedger.getAmount().add(clRequest.amount());
                if (finalRefund.compareTo(finalCLRequest.amount()) != 0 ||
                !finalCLRequest.type().name().equalsIgnoreCase(CustomerLedgerType.REFUND.name())) {
                    log.debug("oldDue + newDue != finalDue");
                    throw new BusinessLogicException("Order Total Amt != payAmt + newDueAmt", "Check due pay calculation");
                }
                customerLedger.setAmount(finalRefund);
                customerLedger.setType(CustomerLedgerType.REFUND);
            }
        }

        orderPage.stream().forEach(o -> o.setPaymentStatus(PaymentStatus.PAID));
        orderRepository.saveAll(orderPage);
        customerLedgerRepository.save(customerLedger);

        Payment entity = paymentMapper.toEntity(paymentRequest);
        entity.setReceiver(staff);
        entity.setCustomer(customer);
        Payment savePayment = paymentRepository.save(entity);

        return paymentMapper.toResponseDto(savePayment);
    }

    private Page<Order> getOrderPage(List<Long> orderIds, Long customerId, Long shopId) {
        PageRequest pageRequest = PageRequest.of(0,
                orderIds.size(), Sort.Direction.ASC, "orderDate");

        Page<Order> orders = orderRepository.findAllByIdInAndCustomerIdAndPaymentStatusAndCustomerShopId(
                orderIds, customerId, PaymentStatus.PENDING, shopId, pageRequest
        );
        if (orderIds.size() != orders.getSize()) {
            log.debug("OrderIds and PENDING orders mismatched");
            throw new BusinessLogicException("OrderIds and PENDING orders mismatched", orderIds);
        }

        return orders;
    }

    @Override
    @Transactional
    public PaymentResponseDto createByAmount(Long shopId, Long customerId, PaymentRequestDto requestDto) {

        ShopStaff staff = getCurrentStaff(shopId);
        Customer customer = fetchCustomer(customerId, shopId);
        if (customer.getBlock()) {
            log.warn("Customer is blocked");
            throw new CustomerIsBlockedException(customerId);
        }

        Payment entity = paymentMapper.toEntity(requestDto);
        entity.setReceiver(staff);
        entity.setCustomer(customer);

        CustomerLedger customerLedger = getCustomerLedger(shopId, customerId);

        BigDecimal currentAmount = customerLedger.getAmount()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal paymentAmount = requestDto.amount()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal newAmount;

        if (customerLedger.getType() == CustomerLedgerType.DUE) {

            int compare = paymentAmount.compareTo(currentAmount);

            if (compare > 0) {
                // Overpayment → becomes REFUND
                newAmount = paymentAmount.subtract(currentAmount);
                customerLedger.setType(CustomerLedgerType.REFUND);

            } else if (compare < 0) {
                // Partial payment → still DUE
                newAmount = currentAmount.subtract(paymentAmount);
                customerLedger.setType(CustomerLedgerType.DUE);

            } else {
                // Exact payment → zero
                newAmount = BigDecimal.ZERO;
                customerLedger.setType(CustomerLedgerType.DUE); // or keep previous
            }

        } else {
            // Existing REFUND → payment increases refund
            newAmount = currentAmount.add(paymentAmount);
            customerLedger.setType(CustomerLedgerType.REFUND);
        }

        customerLedger.setAmount(newAmount.abs().setScale(2, RoundingMode.HALF_UP));
        Payment newPayment = paymentRepository.save(entity);
        return paymentMapper.toResponseDto(newPayment);
    }

    private CustomerLedger getCustomerLedger(Long shopId, Long customerId) {
        return customerLedgerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    log.debug("Customer's dueRefund not found customerId-{} in shopId-{}", customerId, shopId);
                    return new EntityNotFoundException("Previous balance record not found", "check due-refund data");
                });
    }

    private void validatePayment(
            PaymentRequestDto pay, Page<Order> orderPage,
            CustomerLedgerRequestDto customerLedgerRequestDto
    ) {
        BigDecimal total = orderPage
                .stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal lastOrderAmount = orderPage.getContent()
                .getLast().getTotalAmount();

        validateDueOrRefund(total, pay.amount(), lastOrderAmount, customerLedgerRequestDto);
    }

    private Page<Order> validateFetchOrders(Long shopId, Long customerId, List<Long> orderIds) {
        PageRequest pageRequest = PageRequest.of(0,
                orderIds.size(), Sort.Direction.ASC, "orderDate");
        Page<Order> orderPage = orderRepository
                .findAllByIdInAndCustomerIdAndPaymentStatusAndCustomerShopId(
                        orderIds,
                        customerId,
                        PaymentStatus.PENDING,
                        shopId,
                        pageRequest
                );
        if (isInvalidFetchedOrders(orderIds, orderPage)) {
            log.warn("{fetchedOrderPage, provided orderIds size is different}");
            throw new PaymentValidationFailed("fetched orders and provided orderIds are not same");
        }
        return orderPage;
    }

    private void validateDueOrRefund(
            BigDecimal total,
            BigDecimal payAmount,
            BigDecimal lastOrderAmount,
            CustomerLedgerRequestDto customerLedgerRequestDto
    ) {
        int compare = total.compareTo(payAmount);

        if (compare > 0) {
            if (customerLedgerRequestDto == null) {
                log.warn("If customerLedgerRequestDto is not NULL");
                throw new PaymentValidationFailed("Required CustomerLedger");
            }
            // total > payAmount → DUE
            BigDecimal dueAmount = total.subtract(payAmount);
            if (dueAmount.compareTo(customerLedgerRequestDto.amount()) != 0) {
                throw new PaymentValidationFailed("Due calculation error",
                        String.format("Due expected amount=%s and provided amount=%s",
                                dueAmount, customerLedgerRequestDto.amount()));
            }

            if (dueAmount.compareTo(lastOrderAmount) >= 0) {
                log.warn("Mistake in order selection");
                throw new PaymentValidationFailed("Amount is not calculated properly",
                        String.format("Due amount Rs.%s must be less than last order amount Rs.%s that case dis select last order",
                                dueAmount, lastOrderAmount));
            }
            if (customerLedgerRequestDto.type() != CustomerLedgerType.DUE) {
                throw new PaymentValidationFailed("This type is due");
            }
            if (dueAmount.compareTo(customerLedgerRequestDto.amount()) < 0) {
                log.warn("Invalid DUE calculation");
                throw new PaymentValidationFailed("Amount is not calculated properly",
                        String.format("Due amount expected=%s but provided amount=%s",
                                dueAmount, customerLedgerRequestDto.amount()));
            }
            return;
        } else if (compare < 0) {
            if (customerLedgerRequestDto == null) {
                log.warn("CustomerLedgerRequestDto is not NULL");
                throw new PaymentValidationFailed("Required DueOrRefundRequest");
            }
            // total < payAmount → REFUND
            BigDecimal refundAmount = payAmount.subtract(total);
            if (refundAmount.compareTo(customerLedgerRequestDto.amount()) != 0) {
                log.warn("Mistake in REFUND calculation");
                throw new PaymentValidationFailed("Amount is not calculated properly",
                        String.format("Refund amount expected=%s but provided amount=%s",
                                refundAmount, customerLedgerRequestDto.amount()));
            }

            if (customerLedgerRequestDto.type() != CustomerLedgerType.REFUND) {
                throw new PaymentValidationFailed("This is Refund Type not Due");
            }
            return;
        }

        // If total == payAmount → should be no DUE or REFUND
        if (customerLedgerRequestDto != null) {
            log.warn("No due/refund should exist when total == payAmount");
            throw new PaymentValidationFailed("Payment type mismatch with zero balance");
        }
    }

    private boolean isInvalidFetchedOrders(List<Long> orderIds, Page<Order> orderPage) {
        List<Long> fetchedIds = orderPage.stream().map(Order::getId).toList();

        if (fetchedIds.size() != orderIds.size() ||
                !new HashSet<>(fetchedIds).containsAll(orderIds)) {
            throw new PaymentValidationFailed("Fetched orders mismatch with provided IDs or some already paid");
        }
        return orderPage.getContent().size() != orderIds.size() || !new HashSet<>(orderIds).containsAll(fetchedIds);
    }

    @Transactional
    @Override
    public PaymentResponseDto update(Long shopId, Long paymentId, PaymentRequestDto requestDto) {
        Payment payment = fetchPayment(paymentId, shopId);
        PaymentResponseDto before = paymentMapper.toResponseDto(payment);
        StringBuilder fields = new StringBuilder("[");
        if (requestDto.mode() != null
                && !payment.getMode().equals(requestDto.mode())
        ) {
            payment.setMode(requestDto.mode());
            fields.append(", mode");
        }
        if (!payment.getAmount().equals(requestDto.amount())) {
            payment.setAmount(requestDto.amount());
            fields.append(", amount");
        }
        Payment updatedPayment = paymentRepository.save(payment);
        PaymentResponseDto responseDto = paymentMapper.toResponseDto(updatedPayment);
        shopOperationService.updateModule(shopId, getCurrentStaff(shopId).getId(), Module.PAYMENT, responseDto.id(),
                fields.toString(), before.toString(), responseDto.toString());
        return responseDto;
    }

    @Override
    @Transactional
    public void deleteById(Long shopId, Long paymentId) {
        Payment payment = fetchPayment(paymentId, shopId);
        PaymentResponseDto before = paymentMapper.toResponseDto(payment);
        payment.setDeleted(true);
        paymentRepository.save(payment);
        shopOperationService.deleteModule(shopId, getCurrentStaff(shopId).getId(), Module.PAYMENT,
                before.id(), before.toString());
    }

    // Utils methods
    private ShopStaff getCurrentStaff(Long shopId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((User) auth.getPrincipal()); // username stored in token
        if (user == null) {
            log.error("User by-passed security");
            throw new UserNotFoundException("UnAuthorize access denied",
                    "Without authority you enter in application");
        }
        return shopStaffRepository.findByShopIdAndUserDetailUserIdAndActiveTrue(shopId, user.getId())
                .orElseThrow(() -> new StaffNotFoundException("Staff not found for this shop"));
    }

    private Customer fetchCustomer(Long customerId, Long shopId) {
        return customerRepository.findByIdAndShopId(customerId, shopId)
                .orElseThrow(() -> {
                    log.warn("Customer not found");
                    return new CustomerBelongToShopException(customerId, shopId);
                });
    }

    private Payment fetchPayment(Long paymentId, Long shopId) {
        return paymentRepository.findByIdAndDeletedFalseAndCustomerShopId(paymentId, shopId)
                .orElseThrow(() -> {
                    log.warn("Payment not found");
                    return new PaymentNotFoundException(paymentId);
                });
    }
}
