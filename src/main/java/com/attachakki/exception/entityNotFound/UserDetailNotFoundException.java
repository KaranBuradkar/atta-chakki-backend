package com.attachakki.exception.entityNotFound;

public class UserDetailNotFoundException extends EntityNotFoundException{
    public UserDetailNotFoundException(String message, Object data) {
        super(message, data);
    }

    public UserDetailNotFoundException(Long userDetailId) {
        this("no userDetails found with id "+userDetailId, null);
    }

    public UserDetailNotFoundException(String username) {
        this("no userDetails found with username "+username, null);
    }
}
