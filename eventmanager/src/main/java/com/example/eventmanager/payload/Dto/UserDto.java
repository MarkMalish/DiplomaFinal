package com.example.eventmanager.payload.Dto;

import com.example.eventmanager.model.User;
import org.springframework.stereotype.Component;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean blocked;
    public UserDto(Long id, String username, String email, String role, boolean blocked) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.blocked = blocked;
    }

        public Long getId() {
            return id;
        }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
// геттеры и сеттеры
}

