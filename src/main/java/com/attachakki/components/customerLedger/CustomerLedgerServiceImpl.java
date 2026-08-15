package com.attachakki.components.customerLedger;

import com.attachakki.exception.entityNotFound.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerLedgerServiceImpl implements CustomerLedgerService{

    private static final Logger log = LoggerFactory.getLogger(CustomerLedgerServiceImpl.class);
    private final CustomerLedgerRepository customerLedgerRepository;
    private final CustomerLedgerMapper customerLedgerMapper;

    public CustomerLedgerServiceImpl(CustomerLedgerRepository customerLedgerRepository,
                                     CustomerLedgerMapper customerLedgerMapper) {
        this.customerLedgerRepository = customerLedgerRepository;
        this.customerLedgerMapper = customerLedgerMapper;
    }

    @Override
    public CustomerLedgerResponseDto create(Long shopId, Long customerId, CustomerLedgerRequestDto requestDto) {
        return null;
    }

    @Override
    public CustomerLedgerResponseDto findCustomerLedger(Long shopId, Long customerId) {
        CustomerLedger customerLedger = customerLedgerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    log.debug("CustomerLedger not found, CUSTOMER_ID_{}", customerId);
                    return new EntityNotFoundException("CustomerLedger not found", "CUSTOMER_ID_" + customerId);
                });
        return customerLedgerMapper.toResponseDto(customerLedger);
    }
}
