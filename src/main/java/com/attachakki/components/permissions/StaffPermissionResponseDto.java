package com.attachakki.components.permissions;

import com.attachakki.entity.type.PermissionLevel;
import com.attachakki.entity.type.Module;

public record StaffPermissionResponseDto(
        Long id, Module module, PermissionLevel level
) {
}
