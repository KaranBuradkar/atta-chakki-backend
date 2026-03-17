package com.atachakki.components.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByShopIdAndDeletedFalse(Long shopId, PageRequest of);

    // search filters:
    Page<Customer> findByShopIdAndNameContainingAndDeletedFalse(Long shopId, String search, PageRequest of);

    Optional<Customer> findByIdAndShopId(Long customerId, Long shopId);

    Optional<Integer> countByShopId(Long id);
}