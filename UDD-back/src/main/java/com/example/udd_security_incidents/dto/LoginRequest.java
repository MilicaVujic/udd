package com.example.udd_security_incidents.dto;


public class LoginRequest {
    private String username;
    private String password;

    // Getteri i setteri
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
