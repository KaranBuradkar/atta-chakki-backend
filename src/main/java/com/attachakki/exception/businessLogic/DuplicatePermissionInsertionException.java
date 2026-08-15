package com.attachakki.exception.businessLogic;

import java.util.List;

public class DuplicatePermissionInsertionException extends BusinessLogicException {
    public DuplicatePermissionInsertionException(String message, List<String> details) {
        super(message, details);
    }
}
