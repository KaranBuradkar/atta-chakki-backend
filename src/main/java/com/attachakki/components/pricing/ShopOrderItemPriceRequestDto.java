package com.attachakki.components.pricing;

import com.attachakki.entity.type.QuantityType;
import com.attachakki.validation.PriceFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ShopOrderItemPriceRequestDto {

    @NotBlank(message = "Order item is required")
    private String orderItemName;

    @NotNull(message = "Quantity type is required")
    private QuantityType quantityType;

    @PriceFormat(message = "Invalid price, It cannot be null, negative and more than 2 decimal places")
    private BigDecimal unitPrice;

    @NotNull(message = "Available status required")
    private Boolean available = true;

    public String getOrderItemName() {
        return orderItemName;
    }

    public void setOrderItemName(String orderItemName) {
        this.orderItemName = orderItemName;
    }

    public QuantityType getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(QuantityType quantityType) {
        this.quantityType = quantityType;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
