package com.attachakki.security;

import com.attachakki.entity.User;
import com.attachakki.exception.entityNotFound.UserNotFoundException;
import com.attachakki.exception.validation.InvalidJwtTokenException;
import com.attachakki.repository.StaffPermissionRepository;
import com.attachakki.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class JwtAuthFilter extends OncePerRequestFilter {

    Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver exceptionResolver;
    private final StaffPermissionRepository staffPermissionRepository;

    public JwtAuthFilter(
            JwtService jwtService,
            UserRepository userRepository,
            HandlerExceptionResolver handlerExceptionResolver,
            StaffPermissionRepository staffPermissionRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.exceptionResolver = handlerExceptionResolver;
        this.staffPermissionRepository = staffPermissionRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            System.out.println("---next request---");
            log.debug("incoming request: {} {}", request.getMethod(), request.getRequestURI());

            long start = System.currentTimeMillis();

            // 1. Skip if user already authenticated or request for public apis
            if (shouldSkip(request) || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. Extract Jwt token from bearer
            final String token = extractToken(request);

            if (token == null) {
                // Skip for OAuth/public APIs, allow other security filters (OAuth2)
                if (shouldSkip(request)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Protected API → must have JWT
                log.warn("Missing Bearer token");
                throw new InvalidJwtTokenException("Missing Bearer token", null);
            }

            try {
                jwtService.isValidToken(token);
            } catch (Exception e) {
                log.warn("Invalid token signature");
                throw new InvalidJwtTokenException("Invalid token", "Invalid token signature");
            }

            if (jwtService.isExpiredToken(token)) {
                log.warn("Invalid token signature or expired token");
                throw new InvalidJwtTokenException("Invalid token signature or expired token", token);
            }

            // 3. Extract username from token
            String username = jwtService.getUsernameFromToken(token);
            if (username == null) {
                log.warn("Invalid token signature");
                throw new InvalidJwtTokenException("Invalid token signature", token);
            }

            // 4. get user from repository
            User user = userRepository.findUserWithRolesByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("No user found", null));

            Collection<? extends GrantedAuthority> authorities = importAuthority(user, request);

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(user, null, authorities);

            // 5. set user is authenticated
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.debug("JWT filter executed in {} ms", System.currentTimeMillis() - start);

        } catch (Exception e) {
            log.error("Jwt filter error: {}", e.getMessage());
            exceptionResolver.resolveException(request, response, null, e);
        }

        // 6. Continue filters
        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> importAuthority(
            User user,
            HttpServletRequest request
    ) {
        Long shopId = extractShopId(request);

        // staff authorities ex. SHOP_12_CUSTOMER_FULL, SHOP_12_ORDER_READ
        List<SimpleGrantedAuthority> authorities =
                new ArrayList<>(getStaffAuthorities(shopId, user.getId()));

        // Shops staff role OWNER, SHOPKEEPER, SUPPORTER
        List<String> staffRoles = staffPermissionRepository.findStaffRoles(user.getId());
        if (!staffRoles.isEmpty()) {
            authorities.addAll(staffRoles.stream().map(SimpleGrantedAuthority::new).toList());
        }
        // System role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getSystemRole().name()));
        return new ArrayList<>(authorities);
    }

    private Collection<SimpleGrantedAuthority> getStaffAuthorities(Long shopId, Long userId) {
        // Not shop specific request
        if (shopId == null || shopId < 1) {
            List<String> staffAuthority = staffPermissionRepository
                    .findStaffAuthority(userId);
            return staffAuthority.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        // shop specific request
        List<String> staffPermissions = staffPermissionRepository
                .findStaffAuthorityByShopId(shopId, userId);
        if (!staffPermissions.isEmpty()) {
            return staffPermissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
        return List.of();
    }

    private Long extractShopId(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        for (int i = 0; i < split.length; i++) {
            if ("shops".equals(split[i]) && i + 1 < split.length) {
                try {
                    return Long.parseLong(split[i + 1]);
                } catch (Exception e) {
                    log.debug("not a shopId");
                }
                return null;
            }
        }
        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.isBlank() || uri.equals("/") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-resources") ||
                uri.startsWith("/webjars") ||
                uri.startsWith("/v3/api-docs/swagger-config") ||
                uri.startsWith("/public") ||
                uri.startsWith("/auth/login") ||
                uri.startsWith("/auth/register") ||
                uri.startsWith("/oauth2") ||
                uri.startsWith("/login/oauth2") ||
                uri.startsWith("/error");
    }
}
