package com.attachakki.components.operation;

import com.attachakki.entity.type.Module;
import com.attachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface ShopOperationService {

    @PreAuthorize("@permissionGuard.check(#shopId, 'OPERATION', 'READ')")
    Page<ShopOperationResponseDto> findShopOperation(
            Long shopId, Integer page, Integer size, String direction, String sort);

    void createModule(Long shopId, Long staffId, Module module, Long entityId, String afterValues);

    void updateModule(Long shopId, Long staffId, Module module, Long entityId,
                      String changedFields, String beforeValues, String afterValues);

    void deleteModule(Long shopId, Long staffId, Module module, Long entityId, String beforeValues);

    @IsAdminOrShopOwnerOrShopkeeper
    void delete(Long shopId, Long operationId);
}
