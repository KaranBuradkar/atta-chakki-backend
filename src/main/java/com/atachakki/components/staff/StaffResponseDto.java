package com.atachakki.components.staff;

import com.atachakki.entity.type.StaffRole;

public record StaffResponseDto(
        Long id,
        String shopName,
        String staffName,
        String username,
        Boolean active,
        StaffRole staffRole,
        String addedByName
){
}
