package com.atachakki.components.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndDeletedFalseAndCustomerShopId(Long paymentId, Long shopId);

    Page<Payment> findByDeletedFalseAndCustomerShopIdAndCustomerId(Long shopId, Long customerId, PageRequest of);

    @Query(value = "SELECT COALESCE(sum(amount)) from Payment where customer.shop.id =:shopId")
    Optional<BigDecimal> totalCollectionAmount(Long shopId);
}