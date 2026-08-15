package com.attachakki.components.customerLedger;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerLedgerRepository extends JpaRepository<CustomerLedger, Long> {

    Optional<CustomerLedger> findByCustomerId(Long customerId);
}