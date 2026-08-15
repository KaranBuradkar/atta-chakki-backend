package com.attachakki.exception.entityNotFound;

public class UsernameNotFoundException extends EntityNotFoundException {
    public UsernameNotFoundException(String username) {
        this("User not found with username "+username, null);
    }

    public UsernameNotFoundException(String message, Object data) {
        super(message, data);
    }
}
