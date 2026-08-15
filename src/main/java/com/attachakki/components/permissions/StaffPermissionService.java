package com.attachakki.components.permissions;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StaffPermissionService {

    @PreAuthorize(value = "@permissionGuard.check(#shopId, 'STAFF_PERMISSION', 'READ')")
    Page<StaffPermissionResponseDto> findStaffPermissions(
            Long shopId, Long staffId, Integer page, Integer size, String direction, String[] sort);

    @PreAuthorize(value = "@permissionGuard.check(#shopId, 'STAFF_PERMISSION', 'READ')")
    Page<PermissionResponseDto> findPermissions(Long shopId, Long staffId, Integer page,
                                                Integer size, String direction, String[] sort);

    @PreAuthorize(value = "@permissionGuard.check(#shopId, 'STAFF_PERMISSION', 'WRITE')")
    List<StaffPermissionResponseDto> create(
            Long shopId, Long staffId, @Valid List<StaffPermissionRequestDto> requestDto);

    @PreAuthorize(value = "@permissionGuard.check(#shopId, 'STAFF_PERMISSION', 'DELETE')")
    void delete(Long shopId, List<Long> permissionIds);
}
