package com.attachakki.components.staff;

import com.attachakki.entity.type.StaffRole;

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
