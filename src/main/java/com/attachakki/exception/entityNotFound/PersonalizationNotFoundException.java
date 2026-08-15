package com.attachakki.exception.entityNotFound;

public class PersonalizationNotFoundException extends EntityNotFoundException{
    public PersonalizationNotFoundException(Long userDetailId) {
        super("Personalization not found with userDetailId-"+userDetailId, null);
    }
}
