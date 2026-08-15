package com.attachakki.security;

import com.attachakki.config.JwtProperties;
import com.attachakki.entity.UserToken;
import com.attachakki.entity.User;
import com.attachakki.exception.entityNotFound.RefreshTokenNotFoundException;
import com.attachakki.repository.UserTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final JwtProperties jwtProperties;
    private final UserTokenRepository userTokenRepository;

    public JwtService(
            JwtProperties jwtProperties,
            UserTokenRepository userTokenRepository
    ) {
        this.jwtProperties = jwtProperties;
        this.userTokenRepository = userTokenRepository;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(User user, Long expiration) {
        return Jwts.builder()
                .subject(user.getUsername())
                .signWith(getSecretKey())
                .claim("user_id", user.getId().toString())
                .claim("system_role", user.getSystemRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    public Date getExpiry(String token) {
        Claims chaim = getChaim(token);
        return chaim.getExpiration();
    }

    private Claims getChaim(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtProperties.accessTokenExpiration());
    }

    public User isValidRefreshToken(String refreshToken) {
        return getUserFromRefreshToken(refreshToken);
    }

    private User getUserFromRefreshToken(String refreshToken) {
        UserToken userToken = userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(
                        () -> new RefreshTokenNotFoundException("refresh-token not found", null)
                );
        return userToken.getUser();
    }

    @Transactional
    public String generateNewRefreshToken(User user) {
        String refreshToken = generateToken(user, jwtProperties.refreshTokenExpiration());
        UserToken saveUserToken = userTokenRepository.save(
                new UserToken(refreshToken, user)
        );
        return saveUserToken.getRefreshToken();
    }

    @Transactional
    public String updateRefreshToken(User user) {
        UserToken dbUserToken = fetchUserTokenByUser(user);
        String newRefreshToken = generateToken(user, jwtProperties.refreshTokenExpiration());
        dbUserToken.setRefreshToken(newRefreshToken);
        UserToken userToken1 = userTokenRepository.save(dbUserToken);
        return userToken1.getRefreshToken();
    }

    private UserToken fetchUserTokenByUser(User user) {
        return userTokenRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found for userId {}", user.getId().toString());
                    return new RefreshTokenNotFoundException("No refresh-token found", null);
                });
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims chaim = getChaim(token);
            return chaim.getSubject();
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    public boolean isExpiredToken(String token) {
        Date expiry = getExpiry(token);
        return expiry.before(new Date());
    }

    public void isValidToken(String token) {
        getChaim(token);
    }
}
