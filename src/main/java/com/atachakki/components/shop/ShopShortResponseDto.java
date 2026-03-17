package com.atachakki.components.shop;

import com.atachakki.entity.type.StaffRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ShopShortResponseDto(
        Long id,
        String name,
        String owner,
        StaffRole staffRole
) {
}
