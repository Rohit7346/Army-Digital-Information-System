package com.AdisApplication.AdisApplication.dto;

public class AuthResponse {
    private boolean success;
    private String token;
    private String username;
    private String role;
    private String message;

    // Constructors, getters, and setters
    public AuthResponse() {}

    public AuthResponse(boolean success, String token, String username, String role) {
        this.success = success;
        this.token = token;
        this.username = username;
        this.role = role;
    }

    // Getters and setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}