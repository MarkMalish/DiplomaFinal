package com.example.eventmanager.service;

import com.example.eventmanager.model.User;
import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}