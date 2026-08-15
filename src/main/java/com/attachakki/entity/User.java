package com.attachakki.entity;

import com.attachakki.entity.type.AuthProvider;
import com.attachakki.entity.type.SystemRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
        name = "user",
        uniqueConstraints = @UniqueConstraint(columnNames = "username")
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            unique = true,
            length = 120,
            columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin"
    )
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "system_role", length = 50)
    @Enumerated(EnumType.STRING)
    private SystemRole systemRole;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User() {}

    public User(
            String username, String password,
            SystemRole systemRole, AuthProvider provider
    ) {
        this.username = username;
        this.password = password;
        this.systemRole = systemRole;
        this.provider = provider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(systemRole.name()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SystemRole getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
}