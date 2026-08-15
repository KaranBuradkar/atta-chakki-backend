package com.attachakki.components.order;

import com.attachakki.entity.type.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, PageRequest of);

    Optional<Order> findByIdAndCustomerShopId(Long orderId, Long shopId);

    @Query(value = "select sum(o.totalAmount) from Order o where o.customer.shop.id = ?1")
    BigDecimal findTotalDebt(Long shopId);

    Page<Order> findAllByIdInAndCustomerIdAndPaymentStatusAndCustomerShopId(
            List<Long> orderIds, Long customerId, PaymentStatus status, Long shopId, PageRequest pageRequest);

    @Query("""
       select COALESCE(SUM(o.totalAmount), 0)
       from Order o
       where o.customer.id = :customerId
       and o.customer.shop.id = :shopId
       and o.paymentStatus = PENDING and o.deleted = false
       """)
    BigDecimal findTotalBalance(
            @Param("shopId") Long shopId,
            @Param("customerId") Long customerId
    );

    Optional<Integer> countByCustomerShopId(Long shopId);

    @Query(value = "select COALESCE(sum(totalAmount)) from Order where customer.shop.id =:shopId " +
            "and paymentStatus = PENDING and deleted = false")
    Optional<BigDecimal> totalBalance(Long shopId);

    @Query("select id from Order where customer.id = :customerId")
    List<Long> findIdsByCustomerId(Long customerId);
}