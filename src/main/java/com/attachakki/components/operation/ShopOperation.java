package com.attachakki.components.operation;

import com.attachakki.components.shop.Shop;
import com.attachakki.components.staff.ShopStaff;
import com.attachakki.entity.type.Module;
import com.attachakki.entity.type.Operation;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_operations")
public class ShopOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop", nullable = false, updatable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private ShopStaff sender;

    @Column(name = "module", nullable = false, updatable = false)
    private Module module;      // Customer, Shop, Staff

    @Column(name = "entity_id", nullable = false, updatable = false)
    private String entityId;        // 12, "CUST-445"

    @Column(name = "operation", nullable = false, updatable = false)
    private Operation operation;       // CREATE / UPDATE / DELETE

    @Column(name = "changed_fields", columnDefinition = "TEXT")
    private String changedFields;   // ["name","phone","email"]

    @Column(name = "before_values", columnDefinition = "TEXT")
    private String beforeValues;    // {"name":"Old","phone":"111"}

    @Column(name = "after_fields", columnDefinition = "TEXT")
    private String afterValues;     // {"name":"New","phone":"222"}

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ShopOperation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public ShopStaff getSender() {
        return sender;
    }

    public void setSender(ShopStaff sender) {
        this.sender = sender;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(String changedFields) {
        this.changedFields = changedFields;
    }

    public String getBeforeValues() {
        return beforeValues;
    }

    public void setBeforeValues(String beforeValues) {
        this.beforeValues = beforeValues;
    }

    public String getAfterValues() {
        return afterValues;
    }

    public void setAfterValues(String afterValues) {
        this.afterValues = afterValues;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}