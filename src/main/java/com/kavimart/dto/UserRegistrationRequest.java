package com.kavimart.dto;

public class UserRegistrationRequest {
    private String name;
    private String email;
    private String password;
    private String role;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}