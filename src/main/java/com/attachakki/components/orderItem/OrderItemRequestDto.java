package com.attachakki.components.orderItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequestDto {

    @NotNull(message = "name is required")
    @NotBlank(message = "name is not blank")
    private String name;

    public OrderItemRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
