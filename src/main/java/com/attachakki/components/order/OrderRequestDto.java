package com.attachakki.components.order;

import com.attachakki.entity.type.PaymentStatus;
import com.attachakki.entity.type.QuantityType;
import com.attachakki.validation.PriceFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderRequestDto {

    @NotNull(message = "Order item name is required")
    private String orderItemName;

    @NotNull(message = "Quantity type is required")
    private QuantityType quantityType;

    @NotNull(message = "Minimum order quantity must be 1")
    private Integer quantity;

    @PriceFormat(message = "Invalid amount, It cannot be null, negative and more than 2 decimal places")
    private BigDecimal totalAmount;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @NotNull(message = "Order's Date is required")
    private Long orderDate = System.currentTimeMillis();

    public OrderRequestDto() {}

    public QuantityType getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(QuantityType quantityType) {
        this.quantityType = quantityType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderItemName() {
        return orderItemName;
    }

    public void setOrderItemName(String orderItemName) {
        this.orderItemName = orderItemName;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }
}
