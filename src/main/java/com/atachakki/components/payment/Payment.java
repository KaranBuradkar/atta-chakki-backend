package com.atachakki.components.payment;

import com.atachakki.components.customer.Customer;
import com.atachakki.components.order.Order;
import com.atachakki.components.staff.ShopStaff;
import com.atachakki.entity.type.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "amount", nullable = false, scale = 2)
    private BigDecimal amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private PaymentMode mode;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_staff_id")
    private ShopStaff receiver;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "payment_date", nullable = false)
    private Long paymentDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    public Payment() {}

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMode getMode() {
        return mode;
    }

    public void setMode(PaymentMode mode) {
        this.mode = mode;
    }

    public Long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ShopStaff getReceiver() {
        return receiver;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setReceiver(ShopStaff receiver) {
        this.receiver = receiver;
    }
}