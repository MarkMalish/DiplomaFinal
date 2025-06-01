package com.example.eventmanager.mapper;

import com.example.eventmanager.model.User;
import com.example.eventmanager.payload.Dto.UserDto;
import org.springframework.stereotype.Component;
import com.example.eventmanager.model.Role;

import java.util.Comparator;
import java.util.List;

@Component
public class UserMapper {

    private static final List<String> ROLE_PRIORITY = List.of("ADMIN", "ORGANIZER", "USER");
    public UserDto toDto(User user) {
        String mainRole = user.getRoles().stream()
                .map(Role::getName)
                .sorted(Comparator.comparingInt(ROLE_PRIORITY::indexOf))
                .findFirst()
                .orElse("UNKNOWN");

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                mainRole,
                user.isBlocked() // ← добавляем флаг блокировки
        );
    }
    public String extractMainRole(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .sorted(Comparator.comparingInt(ROLE_PRIORITY::indexOf))
                .findFirst()
                .orElse("UNKNOWN");
    }

}
