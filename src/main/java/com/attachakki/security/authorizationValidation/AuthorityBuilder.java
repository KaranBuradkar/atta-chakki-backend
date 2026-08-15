package com.attachakki.security.authorizationValidation;

import com.attachakki.entity.type.Module;
import com.attachakki.entity.type.PermissionLevel;
import com.attachakki.entity.type.StaffRole;

public final class AuthorityBuilder {

    private AuthorityBuilder() {}

    public static String shopAuthority(Long shopId, Module module, PermissionLevel level) {
        return "SHOP_" + shopId + "_" + module + "_" + level;
    }

    public static String shopFull(Long shopId, Module module) {
        return "SHOP_" + shopId + "_" + module.name() + "_FULL";
    }

    public static String owner(Long shopId) {
        return "SHOP_" + shopId + "_"+ StaffRole.OWNER.name();
    }

    public static String shopkeeper(Long shopId) {
        return "SHOP_" + shopId + "_"+StaffRole.SHOPKEEPER.name();
    }
}
