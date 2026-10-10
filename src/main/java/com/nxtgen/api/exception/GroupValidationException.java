package com.nxtgen.api.exception;

import java.util.Map;

public class GroupValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public GroupValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
