package com.attachakki.components.staff;

import com.attachakki.entity.type.StaffRole;

public record ShopStaffResponseDto (
        Long id,
        String shopName,

        String staffName,
        String username,
        StaffRole staffRole,
        String addedByName
){
    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", shopName='" + shopName + '\'' +
                ", staffName='" + staffName + '\'' +
                ", username='" + username + '\'' +
                ", staffRole=" + staffRole +
                ", addedByName='" + addedByName + '\'' +
                '}';
    }
}
