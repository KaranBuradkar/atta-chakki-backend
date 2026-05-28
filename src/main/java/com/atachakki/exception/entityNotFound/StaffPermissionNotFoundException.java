package com.atachakki.exception.entityNotFound;

import java.util.List;

public class StaffPermissionNotFoundException extends EntityNotFoundException{
    public StaffPermissionNotFoundException(Long permissionId) {
        super("staff permission not found id "+permissionId, null);
    }
    public StaffPermissionNotFoundException(List<Long> permissionIds) {
        super("staff permissions not found ids "+permissionIds, null);
    }
}
