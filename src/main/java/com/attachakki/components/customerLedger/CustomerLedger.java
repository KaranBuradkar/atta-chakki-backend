package com.attachakki.components.customerLedger;

import com.attachakki.components.customer.Customer;
import com.attachakki.components.staff.ShopStaff;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;

@Entity
@Table(name = "customer_ledger")
public class CustomerLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CustomerLedgerType type;

    @Column(name = "amount", nullable = false, scale = 2)
    private BigDecimal amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerLedgerStatus status;

    @Column(name = "date", nullable = false, updatable = false)
    private Long date;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_id", updatable = false, nullable = false)
    private ShopStaff addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private ShopStaff updatedBy;

    public CustomerLedger() {
    }

    public CustomerLedger(
            Customer customer, CustomerLedgerType type, BigDecimal amount,
            CustomerLedgerStatus status, Long date,
            ShopStaff addedBy, ShopStaff updatedBy) {
        this.customer = customer;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.addedBy = addedBy;
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerLedgerType getType() {
        return type;
    }

    public void setType(CustomerLedgerType type) {
        this.type = type;
    }

    public CustomerLedgerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerLedgerStatus status) {
        this.status = status;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public ShopStaff getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(ShopStaff addedBy) {
        this.addedBy = addedBy;
    }

    public ShopStaff getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(ShopStaff updatedBy) {
        this.updatedBy = updatedBy;
    }
}
