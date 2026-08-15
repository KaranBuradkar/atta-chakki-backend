package com.attachakki.repository;

import com.attachakki.entity.User;
import com.attachakki.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByUser(User user);

    Optional<UserToken> findByRefreshToken(String refreshToken);
}