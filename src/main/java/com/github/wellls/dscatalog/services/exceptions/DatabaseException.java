package com.github.wellls.dscatalog.services.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException() {
        super("Integrity violation");
    }
}
