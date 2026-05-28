package com.atachakki.components.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByShopIdAndDeletedFalse(Long shopId, PageRequest of);

    // search filters:
    Page<Customer> findByShopIdAndNameContainingAndDeletedFalse(Long shopId, String search, PageRequest of);

    Optional<Customer> findByIdAndShopId(Long customerId, Long shopId);

    Optional<Integer> countByShopIdAndDeletedFalse(Long id);

    @Query("""
            select id from Customer where shop.id = :shopId
            """)
    List<Long> findIdsByShopId(Long shopId);
}