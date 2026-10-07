package com.nxtgen.api.dto;

import java.util.Map;

public class ValidationResponse {

    private final boolean valid;
    private final Map<String, String> errors;

    public ValidationResponse(boolean valid, Map<String, String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public boolean isValid() {
        return valid;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
