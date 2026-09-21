package com.kavimart.dto;

import com.kavimart.model.Role;

public class UserResponseDTO {
    private final long id;
    private final String name;
    private final String email;
    private final Role role;

    public UserResponseDTO(long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}