package com.attachakki.repository;

import com.attachakki.entity.User;
import com.attachakki.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

  Optional<UserDetails> findByUser(User user);

  Optional<Boolean> existsByIdAndUserId(Long userDetailsId, Long userId);

  Optional<UserDetails> findByUserUsername(String email);

  Optional<UserDetails> findByUserId(Long id);
}