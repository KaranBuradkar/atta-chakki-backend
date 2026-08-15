package com.attachakki.components.shop;

import com.attachakki.components.address.AddressDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ShopResponseDto(
        Long id, String name,
        String owner, String phoneNo,
        String email, ShopStatus status,
        String locationUrl, AddressDto addressDto
) {
}
