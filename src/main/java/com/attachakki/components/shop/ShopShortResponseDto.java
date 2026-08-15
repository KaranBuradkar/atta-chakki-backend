package com.attachakki.components.shop;

import com.attachakki.entity.type.StaffRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ShopShortResponseDto(
        Long id,
        String name,
        String owner,
        StaffRole staffRole
) {
}
