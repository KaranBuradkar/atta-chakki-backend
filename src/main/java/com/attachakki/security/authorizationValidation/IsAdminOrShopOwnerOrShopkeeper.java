package com.attachakki.security.authorizationValidation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(value = "hasRole('ADMIN') OR " +
        "hasAnyAuthority('SHOP_'+#shopId+'_OWNER', 'SHOP_'+#shopId+'_SHOPKEEPER')")
public @interface IsAdminOrShopOwnerOrShopkeeper {
    String shopId() default "";
}
