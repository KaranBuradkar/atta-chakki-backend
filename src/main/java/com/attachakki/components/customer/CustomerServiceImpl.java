package com.attachakki.components.customer;

import com.attachakki.components.customerLedger.CustomerLedgerRepository;
import com.attachakki.components.customerLedger.CustomerLedgerStatus;
import com.attachakki.components.customerLedger.CustomerLedgerType;
import com.attachakki.components.customerLedger.CustomerLedger;
import com.attachakki.components.operation.ShopOperationService;
import com.attachakki.components.order.OrderRepository;
import com.attachakki.components.shop.Shop;
import com.attachakki.components.staff.ShopStaff;
import com.attachakki.entity.User;
import com.attachakki.entity.type.Module;
import com.attachakki.exception.entityNotFound.CustomerIdNotFoundException;
import com.attachakki.exception.entityNotFound.ShopIdNotFoundException;
import com.attachakki.exception.entityNotFound.StaffNotFoundException;
import com.attachakki.components.shop.ShopRepository;
import com.attachakki.repository.ShopStaffRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final CustomerMapper customerMapper;
    private final ShopStaffRepository shopStaffRepository;
    private final ShopOperationService shopOperationService;
    private final OrderRepository orderRepository;
    private final CustomerLedgerRepository customerLedgerRepository;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            ShopRepository shopRepository,
            CustomerMapper customerMapper,
            ShopStaffRepository shopStaffRepository,
            ShopOperationService shopOperationService,
            OrderRepository orderRepository,
            CustomerLedgerRepository customerLedgerRepository
    ) {
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
        this.customerMapper = customerMapper;
        this.shopStaffRepository = shopStaffRepository;
        this.shopOperationService = shopOperationService;
        this.orderRepository = orderRepository;
        this.customerLedgerRepository = customerLedgerRepository;
    }

    @Override
    public Page<CustomerResponseShortDto> findCustomers(
            Long shopId, Integer page, Integer size,
            String direction, String sort,
            String name
    ) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<Customer> customerPage;
        if (name != null && !name.isBlank()) {
            customerPage = customerRepository.findByShopIdAndNameContainingAndDeletedFalse(
                    shopId, name, PageRequest.of(page, size, dir, sort));
        } else {
            customerPage = customerRepository
                    .findByShopIdAndDeletedFalse(shopId, PageRequest.of(page, size, dir, sort));
        }
        customerPage.forEach(c -> {
            BalanceDto dto = getCustomerBalance(shopId, c);
            c.setBalance(dto.balance);
            c.setType(dto.type);
        });
        return customerPage.map(customerMapper::toResponseShortDto);
    }

    @Override
    public CustomerResponseDto findCustomer(Long shopId, Long customerId) {
        Customer customer = fetchCustomerById(shopId, customerId);
        BalanceDto dto = getCustomerBalance(shopId, customer);
        customer.setBalance(dto.balance);
        customer.setType(dto.type);
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public List<Long> findCustomerIds(Long shopId) {
        return customerRepository.findIdsByShopId(shopId);
    }

    @Override
    @Transactional
    public CustomerResponseDto create(
            Long shopId,
            CustomerRequestDto requestDto
    ) {
        Shop shop = fetchShopByShopId(shopId);
        ShopStaff staff = getCurrentStaff(shopId);

        // create customer entity
        Customer customer = customerMapper.toEntity(requestDto);
        customer.setShop(shop);
        customer.setAddedBy(staff);
        customer.setBlock(false);
        customer.setDeleted(false);
        Customer response = customerRepository.save(customer);
        // create dueRefund entity
        createNewDueRefund(shopId, response, requestDto.getDueRefundType(), requestDto.getBalance(), requestDto.getDate());
        // prepare response
        BalanceDto dto = getCustomerBalance(shopId, customer);
        response.setBalance(dto.balance);
        response.setType(dto.type);
        CustomerResponseDto responseDto = customerMapper.toResponseDto(response);
        shopOperationService.createModule(shopId, response.getUpdatedBy().getId(), Module.CUSTOMER,
                responseDto.id(), stringCustomer(responseDto));
        return responseDto;
    }

    @Override
    public List<CustomerResponseDto> createAll(Long shopId, List<CustomerRequestDto> requestDto) {
        Shop shop = fetchShopByShopId(shopId);
        ShopStaff staff = getCurrentStaff(shopId);

        List<CustomerResponseDto> customers = requestDto.stream().map(req -> {
            // create customer entity
            Customer customer = customerMapper.toEntity(req);
            customer.setShop(shop);
            customer.setAddedBy(staff);
            customer.setBlock(false);
            customer.setDeleted(false);

            Customer response = customerRepository.save(customer);

            // create dueRefund entity
            createNewDueRefund(shopId, response, req.getDueRefundType(), req.getBalance(), req.getDate());
            // prepare response
            BalanceDto dto = getCustomerBalance(shopId, response);
            response.setBalance(dto.balance);
            response.setType(dto.type);
            CustomerResponseDto responseDto = customerMapper.toResponseDto(response);
            shopOperationService.createModule(shopId, response.getUpdatedBy().getId(), Module.CUSTOMER,
                    responseDto.id(), stringCustomer(responseDto));
            return responseDto;
        }).toList();

        return customers;
    }

    private void createNewDueRefund(Long shopId, Customer customer, CustomerLedgerType type, BigDecimal balance, Long date) {
        CustomerLedger customerLedger = new CustomerLedger(customer, type, balance, CustomerLedgerStatus.PENDING, date, getCurrentStaff(shopId), getCurrentStaff(shopId));
        customerLedger.setCustomer(customer);
        customer.setDueOrRefund(customerLedger);
        customerLedgerRepository.save(customerLedger);
    }

    private BalanceDto getCustomerBalance(Long shopId, Customer customer) {
        BigDecimal orderTotal = Optional.ofNullable(orderRepository.findTotalBalance(shopId, customer.getId()))
                .orElse(BigDecimal.ZERO);

        CustomerLedger customerLedger = customerLedgerRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> new CustomerLedger(customer, CustomerLedgerType.DUE, BigDecimal.ZERO, CustomerLedgerStatus.PENDING,
                        System.currentTimeMillis(), null, null));

        BigDecimal signedBalance;
        if (customerLedger.getType() == CustomerLedgerType.DUE) {
            signedBalance = orderTotal.add(customerLedger.getAmount());
        } else {
            signedBalance = orderTotal.subtract(customerLedger.getAmount());
        }

        // ✅ Compute display-ready balance — no mutation!
        BigDecimal absBalance = signedBalance.abs().setScale(2, RoundingMode.HALF_UP);
        CustomerLedgerType prefix = signedBalance.compareTo(BigDecimal.ZERO) >= 0 ? CustomerLedgerType.DUE : CustomerLedgerType.REFUND;
        return new BalanceDto(prefix, absBalance.toString());
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomerBlockStatus(
            Long shopId, Long customerId, Boolean block
    ) {
        ShopStaff staff = getCurrentStaff(shopId);
        Customer customer = fetchCustomerById(shopId, customerId);

        CustomerResponseDto before = customerMapper.toResponseDto(customer);
        customer.setBlock(block);
        customer.setUpdatedBy(staff);
        Customer updatedCustomer = customerRepository.save(customer);
        BalanceDto dto = getCustomerBalance(shopId, customer);
        updatedCustomer.setBalance(dto.balance);
        updatedCustomer.setType(dto.type);
        CustomerResponseDto responseDto = customerMapper.toResponseDto(updatedCustomer);

        shopOperationService.updateModule(
                shopId, updatedCustomer.getUpdatedBy().getId(), Module.CUSTOMER, responseDto.id(),
                "[block, updatedBy]", stringCustomer(before), stringCustomer(responseDto));

        return responseDto;
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomerFields(
            Long shopId, Long customerId,
            CustomerRequestDto requestDto
    ) {
        Customer customer = fetchCustomerById(shopId, customerId);
        CustomerResponseDto before = customerMapper.toResponseDto(customer);
        ShopStaff staff = getCurrentStaff(customer);
        StringBuilder fields = new StringBuilder("[");

        if (validateString(requestDto.getName(), customer.getName())) {
            customer.setName(requestDto.getName());
            fields.append("name,");
        }
        if (validateString(requestDto.getEmail(), customer.getEmail())) {
            customer.setEmail(requestDto.getEmail());
            fields.append(", email");
        }
        if (validateString(requestDto.getSpecification(), customer.getSpecification())) {
            customer.setSpecification(requestDto.getSpecification());
            fields.append(", specification");
        }
        customer.setUpdatedBy(staff);
        fields.append(", updatedBy]");
        Customer saveCustomer = customerRepository.save(customer);
        BalanceDto dto = getCustomerBalance(shopId, saveCustomer);
        saveCustomer.setBalance(dto.balance);
        saveCustomer.setType(dto.type);
        CustomerResponseDto responseDto = customerMapper.toResponseDto(saveCustomer);
        shopOperationService.updateModule(shopId, saveCustomer.getUpdatedBy().getId(),
                Module.CUSTOMER, saveCustomer.getId(),
                fields.toString(), stringCustomer(before), stringCustomer(responseDto));
        return responseDto;
    }

    @Override
    @Transactional
    public void deleteById(Long shopId, Long customerId) {
        ShopStaff staff = getCurrentStaff(shopId);
        Customer customer = fetchCustomerById(shopId, customerId);
        customer.setUpdatedBy(staff);
        customer.setDeleted(true);
        Customer deleted = customerRepository.save(customer);
        CustomerResponseDto responseDto = customerMapper.toResponseDto(deleted);
        shopOperationService.deleteModule(shopId, deleted.getUpdatedBy().getId(), Module.CUSTOMER,
                responseDto.id(), stringCustomer(responseDto));
    }

    @Override
    public Page<CustomerResponseDto> findAllCustomers(
            Long shopId, Integer page, Integer size, String direction, String name) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<Customer> customers = customerRepository
                .findByShopIdAndDeletedFalse(shopId, PageRequest.of(page, size, dir, name));
        customers.forEach(c -> {
            BalanceDto dto = getCustomerBalance(shopId, c);
            c.setBalance(dto.balance);
            c.setType(dto.type);
        });
        return customers.map(customerMapper::toResponseDto);
    }

    // Util methods
    private Customer fetchCustomerById(Long shopId, Long customerId) {
        return customerRepository.findByIdAndShopId(customerId, shopId)
                .orElseThrow(() -> {
                    log.warn("customer id not found");
                    return new CustomerIdNotFoundException(customerId);
                });
    }

    private ShopStaff getCurrentStaff(Customer customer) {
        List<ShopStaff> shopStaffs = customer.getShop().getShopStaffs();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) auth.getPrincipal()).getId(); // username stored in token

        List<ShopStaff> list = shopStaffs.stream()
                .filter(staff -> Objects.equals(staff.getUserDetail().getUser().getId(), userId))
                .toList();
        if (list.size() == 1) {
            return list.getFirst();
        }
        return null;
    }

    private Shop fetchShopByShopId(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.warn("shopId not found");
                    return new ShopIdNotFoundException(shopId);
                }
        );
    }

    private ShopStaff getCurrentStaff(Long shopId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) auth.getPrincipal()).getId(); // username stored in token
        return shopStaffRepository.findByShopIdAndUserDetailUserIdAndActiveTrue(shopId, userId)
                .orElseThrow(() -> new StaffNotFoundException("Staff not found for this shop"));
    }

    private boolean validateString(String req, String exist) {
        return req != null && !req.isBlank() && !req.equals(exist);
    }

    private String stringCustomer(CustomerResponseDto c) {
        return String.format("{id=%s, name=%s, email=%s, specification=%s, block=%s, createdAt=%s}",
                c.id(), c.name(), c.email(), c.specification(), c.block(), c.createdAt());
    }

    private record BalanceDto(CustomerLedgerType type, String balance) {}
}
