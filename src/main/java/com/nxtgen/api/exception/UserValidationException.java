package com.nxtgen.api.exception;

import java.util.Map;

public class UserValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public UserValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
