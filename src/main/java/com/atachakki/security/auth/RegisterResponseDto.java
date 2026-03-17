package com.atachakki.security.auth;

import com.atachakki.entity.type.AuthProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterResponseDto (
        Long userDetailsId, String name,
        String username, String phoneNo,
        String profileUrl, AuthProvider provider,
        String accessToken, String refreshToken
) {}