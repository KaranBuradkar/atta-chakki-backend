package com.atachakki.components.permissions;

public record PermissionResponseDto(
        Long id, Long staffId, Module module,
        Boolean read, Boolean write, Boolean update, Boolean delete
) {
}
