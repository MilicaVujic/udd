package com.example.udd_security_incidents.exceptionhandling.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
