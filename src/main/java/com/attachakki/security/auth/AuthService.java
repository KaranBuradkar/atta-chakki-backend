package com.attachakki.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    RegisterResponseDto register(@Valid RegisterRequestDto requestDto);

    RegisterResponseDto login(@Valid LoginRequestDto requestDto);

    AccessTokenDto refreshToken(HttpServletRequest request);
}
