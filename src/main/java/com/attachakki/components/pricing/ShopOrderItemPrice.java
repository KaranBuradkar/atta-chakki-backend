package com.attachakki.components.pricing;

import com.attachakki.components.orderItem.OrderItem;
import com.attachakki.components.shop.Shop;
import com.attachakki.entity.type.QuantityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(
        name = "shop_order_item_price",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_price_for_shop_item_quantity",
                        columnNames = {"shop_id", "order_item_id", "quantity_type"}
                )
        }
)
public class ShopOrderItemPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Shop is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @NotNull(message = "Order item is required")
    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @NotNull(message = "Quantity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_type", nullable = false, length = 30)
    private QuantityType quantityType;

    @NotNull(message = "Price is required")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "available", nullable = false)
    private Boolean available;

    public ShopOrderItemPrice() {}

    public ShopOrderItemPrice(
            Long id, Shop shop, OrderItem orderItem,
            QuantityType quantityType, BigDecimal unitPrice
    ) {
        this.id = id;
        this.shop = shop;
        this.orderItem = orderItem;
        this.quantityType = quantityType;
        this.unitPrice = unitPrice;
    }

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

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
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
