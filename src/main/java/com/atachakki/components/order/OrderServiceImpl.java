package com.atachakki.components.order;

import com.atachakki.components.customer.Customer;
import com.atachakki.components.customer.CustomerRepository;
import com.atachakki.components.customerLedger.CustomerLedger;
import com.atachakki.components.customerLedger.CustomerLedgerRepository;
import com.atachakki.components.customerLedger.CustomerLedgerType;
import com.atachakki.components.operation.ShopOperationService;
import com.atachakki.components.pricing.ShopOrderItemPrice;
import com.atachakki.components.pricing.ShopOrderItemPriceRepository;
import com.atachakki.components.staff.ShopStaff;
import com.atachakki.entity.User;
import com.atachakki.entity.type.Module;
import com.atachakki.entity.type.PaymentStatus;
import com.atachakki.exception.businessLogic.BusinessLogicException;
import com.atachakki.exception.businessLogic.CustomerIsBlockedException;
import com.atachakki.exception.entityNotFound.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerRepository customerRepository;
    private final ShopStaffRepository shopStaffRepository;
    private final ShopOrderItemPriceRepository shopOrderItemPriceRepository;
    private final ShopOperationService shopOperationService;
    private final CustomerLedgerRepository customerLedgerRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            CustomerRepository customerRepository,
            ShopStaffRepository shopStaffRepository,
            ShopOrderItemPriceRepository shopOrderItemPriceRepository,
            ShopOperationService shopOperationService,
            CustomerLedgerRepository customerLedgerRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerRepository = customerRepository;
        this.shopStaffRepository = shopStaffRepository;
        this.shopOrderItemPriceRepository = shopOrderItemPriceRepository;
        this.shopOperationService = shopOperationService;
        this.customerLedgerRepository = customerLedgerRepository;
    }

    @Override
    public Page<OrderResponseDto> findOrders(
            Long shopId, Long customerId, Integer page,
            Integer size, String direction, String sort
    ) {
        customerRepository.findByIdAndShopId(customerId, shopId).orElseThrow(() -> {
            log.warn("customer is not belong to shop");
            return new CustomerBelongToShopException("customer is not belong to shop");
        });

        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<Order> orderPage = orderRepository
                .findByCustomerId(customerId, PageRequest.of(page, size, dir, sort));
        return orderPage.map(orderMapper::toResponseDto);
    }

    @Override
    public List<Long> findOrderIds(Long shopId, Long customerId) {
        return orderRepository.findIdsByCustomerId(customerId);
    }

    @Override
    public String findTotalCustomerBalance(Long shopId, Long customerId) {
        BigDecimal balance = orderRepository.findTotalBalance(shopId, customerId);
        return balance.toString();
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long shopId, Long customerId, OrderRequestDto requestDto) {
        // validations
        Customer customer = fetchCustomer(customerId, shopId);
        if (customer.getBlock()) {
            log.warn("Customer is blocked");
            throw new CustomerIsBlockedException(customerId);
        }
        ShopOrderItemPrice shopOrderItemPrice = fetchShopOrderItemPrice(shopId, requestDto);
        validateTotalAmount(shopOrderItemPrice, requestDto);

        // entity creation
        ShopStaff staff = getCurrentStaff(shopId);
        Order entity = orderMapper.toEntity(requestDto);
        entity.setDeleted(false);
        entity.setCustomer(customer);
        entity.setAddedBy(staff);
        entity.setShopOrderItemPrice(shopOrderItemPrice);

        // db operation and response creation
        Order newOrder = orderRepository.save(entity);
        OrderResponseDto responseDto = orderMapper.toResponseDto(newOrder);
        shopOperationService.createModule(shopId, newOrder.getAddedBy().getId(),
                Module.ORDER, responseDto.id(), orderToString(responseDto));
        return responseDto;
    }

    @Override
    @Transactional
    public List<OrderResponseDto> createAllOrders(Long shopId, Long customerId, List<OrderRequestDto> orderRequests) {
        // validations
        Customer customer = fetchCustomer(customerId, shopId);
        if (customer.getBlock()) {
            log.warn("Customer is blocked");
            throw new CustomerIsBlockedException(customerId);
        }
        ShopStaff staff = getCurrentStaff(shopId);
        List<Order> newOrders = new ArrayList<>();

        for (OrderRequestDto requestDto : orderRequests) {
            ShopOrderItemPrice shopOrderItemPrice = fetchShopOrderItemPrice(shopId, requestDto);
            validateTotalAmount(shopOrderItemPrice, requestDto);

            // entity creation
            Order entity = orderMapper.toEntity(requestDto);
            entity.setDeleted(false);
            entity.setCustomer(customer);
            entity.setAddedBy(staff);
            entity.setShopOrderItemPrice(shopOrderItemPrice);
            newOrders.add(entity);
        }
        List<Order> orders = orderRepository.saveAll(newOrders);
        List<OrderResponseDto> responses = orders.stream().map(orderMapper::toResponseDto).toList();
        responses.forEach(r -> shopOperationService.createModule(
                shopId, r.addedById(), Module.ORDER, r.id(), orderToString(r)));
        return responses;
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderFields(
            Long shopId, Long orderId,
            OrderRequestDto requestDto
    ) {
        ShopStaff staff = getCurrentStaff(shopId);

        Order order = fetchOrderByIdAndShopId(orderId, shopId);
        OrderResponseDto before = orderMapper.toResponseDto(order);
        StringBuilder fields = new StringBuilder("[");

        if (validateInput(requestDto.getPaymentStatus(), order.getPaymentStatus())) {
            order.setPaymentStatus(requestDto.getPaymentStatus());
            fields.append(", paymentStatus");
        }

        if (validateInput(requestDto.getQuantity(), requestDto.getQuantity())) {
            order.setQuantity(requestDto.getQuantity());
            fields.append(", quantity");
        }

        if (requestDto.getTotalAmount() != null &&
                !(order.getTotalAmount().compareTo(requestDto.getTotalAmount()) > 0)) {
            order.setTotalAmount(requestDto.getTotalAmount());
            fields.append(", totalAmount");
        }

        if (validateInput(requestDto.getQuantityType(), order.getShopOrderItemPrice().getQuantityType())
        ) {
            order.getShopOrderItemPrice().setQuantityType(requestDto.getQuantityType());
            fields.append(", quantityType");
        }

        order.setUpdatedBy(staff);
        fields.append(", updatedBy]");
        Order updatedOrder = orderRepository.save(order);
        OrderResponseDto responseDto = orderMapper.toResponseDto(updatedOrder);
        shopOperationService.updateModule(shopId, updatedOrder.getUpdatedBy().getId(), Module.ORDER,
                responseDto.id(), fields.toString(), orderToString(before), orderToString(responseDto));
        return responseDto;
    }

    private <T> boolean validateInput(T request, T existing) {
        return request != null && !existing.equals(request);
    }

    @Override
    @Transactional
    public void deleteOrder(Long shopId, Long orderId) {
        ShopStaff staff = getCurrentStaff(shopId);
        Order order = fetchOrderByIdAndShopId(orderId, shopId);
        OrderResponseDto before = orderMapper.toResponseDto(order);
        order.setDeleted(true);
        order.setUpdatedBy(staff);
        Order deleted = orderRepository.save(order);
        shopOperationService.deleteModule(shopId, deleted.getUpdatedBy().getId(), Module.ORDER,
                before.id(), orderToString(before));
    }

    @Override
    public BigDecimal findTotalDebt(Long shopId) {
        return orderRepository.findTotalDebt(shopId);
    }

    @Override
    public List<OrderResponseDto> crossOrders(Long shopId, Long customerId,
                                              CrossOrdersRequestDto request) {
        List<Order> orders = orderRepository.findAllById(request.orderIds());
        if (orders.size() != request.orderIds().size()) {
            log.error("Mismatched orderIds size");
            throw new BusinessLogicException("Mismatched orders", null);
        }
        BigDecimal amount = new BigDecimal(request.totalAmount());
        BigDecimal total = orders.stream().map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(amount) != 0) {
            log.error("Mismatched total amount");
            throw new BusinessLogicException("Mismatched total amount", null);
        }

        List<Order> list = orders.stream().filter(o -> !o.getPaymentStatus()
                .equals(PaymentStatus.PENDING)).toList();
        if (!list.isEmpty()) {
            log.error("Some orders are not PENDING");
            throw new BusinessLogicException("Some orders already PAID", null);
        }

        manageCustomerLedger(customerId, total);

        orders.forEach(o -> o.setPaymentStatus(PaymentStatus.PAID));
        List<Order> updated = orderRepository.saveAll(orders);

        return updated.stream().map(orderMapper::toResponseDto).toList();
    }
    // util methods

    private void manageCustomerLedger(Long customerId, BigDecimal total) {

        CustomerLedger customerLedger = customerLedgerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("CustomerLedger not found",
                        "CustomerLedger not found for CustomerId-" + customerId));
        if (customerLedger.getType().equals(CustomerLedgerType.DUE)) {
            BigDecimal finalAmount = customerLedger.getAmount().add(total);
            customerLedger.setAmount(finalAmount);
        } else if (customerLedger.getType().equals(CustomerLedgerType.REFUND)) {
            int compare = customerLedger.getAmount().compareTo(total);
            if (compare == 0) {
                BigDecimal finalAmount = customerLedger.getAmount().subtract(total);
                customerLedger.setAmount(finalAmount);
            }

            if (compare > 0) {
                BigDecimal finalAmount = customerLedger.getAmount().subtract(total);
                customerLedger.setAmount(finalAmount);
            }

            if (compare < 0) {
                BigDecimal finalAmount = total.subtract(customerLedger.getAmount());
                customerLedger.setAmount(finalAmount);
                customerLedger.setType(CustomerLedgerType.DUE);
            }
        }
    }

    private Customer fetchCustomer(Long customerId, Long shopId) {
        return customerRepository.findByIdAndShopId(customerId, shopId)
                .orElseThrow(() -> {
                    log.debug("customer not found with customerId and shopId");
                    return new CustomerBelongToShopException(customerId, shopId);
                });
    }

    private void validateTotalAmount(ShopOrderItemPrice itemPrice, OrderRequestDto requestDto) {
        BigDecimal expectedPrice = itemPrice.getUnitPrice()
                .multiply(BigDecimal.valueOf(requestDto.getQuantity())).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = requestDto.getTotalAmount().setScale(2, RoundingMode.HALF_UP);

        if (expectedPrice.compareTo(totalAmount) != 0) {
            log.debug("Business logic failed: Expected total = {}, Provided = {}", expectedPrice, totalAmount);
            throw new BusinessLogicException(
                    "Total amount calculated incorrectly",
                    "Expected total = " + expectedPrice + ", Provided = " + totalAmount
            );
        }
    }

    private ShopStaff getCurrentStaff(Long shopId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) auth.getPrincipal()).getId(); // username stored in token

        return shopStaffRepository.findByShopIdAndUserDetailUserIdAndActiveTrue(shopId, userId)
                .orElseThrow(() -> new StaffNotFoundException("Staff not found for this shop"));
    }

    private ShopOrderItemPrice fetchShopOrderItemPrice(Long shopId, OrderRequestDto req) {
        return shopOrderItemPriceRepository
                .findByShopIdAndOrderItemNameAndQuantityTypeAndAvailableTrue(
                        shopId, req.getOrderItemName(), req.getQuantityType()
                ).orElseThrow(() -> {
                    log.debug("ShopOrderItemPrice not found");
                    return new ShopOrderItemPriceNotFoundException(
                            "ShopOrderItemPrice not found",
                            String.format(
                                    "ShopOrderItemPrice not found with shopId=%s orderItemName=%s quantityType=%s",
                                    shopId, req.getOrderItemName(), req.getQuantityType()
                            )
                    );
                });
    }

    private Order fetchOrderByIdAndShopId(Long orderId, Long shopId) {
        return orderRepository.findByIdAndCustomerShopId(orderId, shopId)
                .orElseThrow(() -> {
                    log.warn("Order not found with id");
                    return new OrderIdNotFoundException(orderId);
                });
    }

    private String orderToString(OrderResponseDto o) {
        return String.format("{id=%s, orderItemName=%s, quantity=%s, " +
                        "quantityType=%s, totalAmount=%s, paymentStatus=%s, " +
                        "addedByName=%s, updatedByName=%s, orderDate=%s, " +
                        "createdAt=%s, updatedAt=%s}", o.id(), o.orderItemName(),
                o.quantity(), o.quantityType(), o.totalAmount(), o.paymentStatus(),
                o.addedByName(), o.updatedByName(), o.orderDate(), o.createdAt(),
                o.updatedAt());
    }
}
