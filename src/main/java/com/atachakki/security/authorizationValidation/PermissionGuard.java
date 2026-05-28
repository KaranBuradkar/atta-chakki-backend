package com.atachakki.security.authorizationValidation;

import com.atachakki.entity.type.Module;
import com.atachakki.entity.type.PermissionLevel;
import com.atachakki.entity.type.StaffRole;
import com.atachakki.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionGuard {

    private static final Logger log = LoggerFactory.getLogger(PermissionGuard.class);

    private final SecurityService securityService;

    public PermissionGuard(SecurityService securityService) {
        this.securityService = securityService;
    }

    public boolean check(Long shopId, Module module, PermissionLevel level) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return this.check(authentication, shopId, module, level);
    }

    public boolean isOwner(Long shopId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var authorities = authentication.getAuthorities();
        String required = AuthorityBuilder.owner(shopId);
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals(required))) {
            log.debug("ACCESS GRANTED: '{}' as {}", authentication.getName(), AuthorityBuilder.owner(shopId));
            return true;
        } else log.debug("ACCESS DENIED: '{}' for {}", authentication.getName(), StaffRole.OWNER);
        return false;
    }

    /**
     * Check permission. Returns true if access is allowed.
     */
    public boolean check(Authentication authentication, Long shopId, Module module, PermissionLevel level) {

        var authorities = authentication.getAuthorities();

        log.debug("Checking permission → shopId={}, module={}, level={}, user={}",
                shopId, module, level, authentication.getName());

        // ADMIN bypass
        if (authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("ADMIN"))) {
            log.info("ACCESS GRANTED: '{}' used ADMIN", authentication.getName());
            return true;
        }

        // OWNER / SHOPKEEPER bypass
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals(AuthorityBuilder.owner(shopId)))) {
            log.debug("ACCESS GRANTED: '{}' used {}", authentication.getName(), AuthorityBuilder.owner(shopId));
            return true;
        }
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals(AuthorityBuilder.shopkeeper(shopId)))) {
            log.debug("ACCESS GRANTED: '{}' used {}", authentication.getName(), AuthorityBuilder.shopkeeper(shopId));
            return true;
        }

        // Non-read permissions require active shop
        if (!PermissionLevel.READ.equals(level) && !securityService.isShopActive(shopId)) {
            log.warn("ACCESS DENIED: shop={} is not active", shopId);
            return false;
        }

        String exact = AuthorityBuilder.shopAuthority(shopId, module, level);
        String full = AuthorityBuilder.shopFull(shopId, module);

        // Check exact or full
        var matched = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.equals(exact) || auth.equals(full))
                .findFirst();

        if (matched.isPresent()) {
            log.info("ACCESS GRANTED: '{}' used authority {}", authentication.getName(), matched.get());
            return true;
        }

        log.warn("ACCESS DENIED: '{}' has no required authority for SHOP={} MODULE={} LEVEL={}",
                authentication.getName(), shopId, module, level);

        return false;
    }
}