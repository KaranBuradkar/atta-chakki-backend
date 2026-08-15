package com.attachakki.components.operation;

import com.attachakki.entity.type.Module;
import com.attachakki.entity.type.Operation;

import java.time.LocalDateTime;

public record ShopOperationResponseDto(
        Long id,
        String shopName,
        String sender,
        Module module,
        String entityId,
        Operation operation,
        String changedFields,
        String beforeValues,
        String afterValues,
        LocalDateTime createdAt
) {}
