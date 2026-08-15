package com.attachakki.components.customer;

public record CustomerResponseDto (
    Long id, String name, String email,
    String specification, Boolean block,
    String balance,
    Long createdAt, Long updatedAt
) {}
