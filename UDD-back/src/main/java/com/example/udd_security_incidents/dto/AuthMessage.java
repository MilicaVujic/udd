package com.example.udd_security_incidents.dto;

public class AuthMessage {
    private String message;
    public AuthMessage(){}

    public AuthMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
