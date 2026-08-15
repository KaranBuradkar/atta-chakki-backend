package com.attachakki.components.pricing;

import com.attachakki.entity.type.QuantityType;
import java.math.BigDecimal;

public record ShopOrderItemPriceResponseDto (
    Long id,
    Long orderItemId,
    String orderItemName,
    QuantityType quantityType,
    BigDecimal unitPrice,
    Boolean available
){
    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", orderItemId=" + orderItemId +
                ", orderItemName='" + orderItemName + '\'' +
                ", quantityType=" + quantityType +
                ", unitPrice=" + unitPrice +
                ", available=" + available +
                '}';
    }
}
