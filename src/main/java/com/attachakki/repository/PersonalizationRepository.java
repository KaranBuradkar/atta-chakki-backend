package com.attachakki.repository;

import com.attachakki.components.personalization.Personalization;
import com.attachakki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalizationRepository extends JpaRepository<Personalization, User> {
    Optional<Personalization> findByUserDetailId(Long userDetailId);
}