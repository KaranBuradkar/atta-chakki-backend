package com.attachakki.exception.databaseException;

import com.attachakki.exception.AppException;

public class DatabaseException extends AppException {

    public DatabaseException(String message, Object data) {
        super(message, data);
    }

}
