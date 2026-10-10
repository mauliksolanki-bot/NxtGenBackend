package com.nxtgen.api.exception;

import java.util.Map;

public class RoleValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public RoleValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
