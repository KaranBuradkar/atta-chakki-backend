package com.attachakki.components.user.details;

public record UserDetailResponseDto(
        Long id,
        String name,
        String username,
        String phoneNo,
        String profileUrl,
        String createdAt
) {
}
