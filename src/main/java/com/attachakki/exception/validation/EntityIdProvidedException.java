package com.attachakki.exception.validation;

public class EntityIdProvidedException extends ValidationException {
    public EntityIdProvidedException(String entity) {
        super("do not provided "+entity+" id", null);

    }
}
