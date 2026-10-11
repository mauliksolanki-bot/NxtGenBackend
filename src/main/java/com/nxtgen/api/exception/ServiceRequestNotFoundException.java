package com.nxtgen.api.exception;

public class ServiceRequestNotFoundException extends RuntimeException {

    public ServiceRequestNotFoundException(String message) {
        super(message);
    }
}
