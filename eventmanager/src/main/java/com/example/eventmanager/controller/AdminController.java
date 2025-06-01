package com.example.eventmanager.controller;

import com.example.eventmanager.mapper.UserMapper;
import com.example.eventmanager.model.Role;
import com.example.eventmanager.model.User;
import com.example.eventmanager.payload.Dto.UserDto;
import com.example.eventmanager.payload.Dto.UpdateUserRoleRequest;
import com.example.eventmanager.repository.EventRepository;
import com.example.eventmanager.repository.RegistrationRepository;
import com.example.eventmanager.repository.RoleRepository;
import com.example.eventmanager.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepo;
    private final EventRepository eventRepo;
    private final RegistrationRepository regRepo;
    private final UserMapper userMapper;
    private final RoleRepository roleRepo;


    public AdminController(UserRepository userRepo, EventRepository eventRepo, RoleRepository roleRepo,
                           RegistrationRepository regRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.eventRepo = eventRepo;
        this.regRepo = regRepo;
        this.roleRepo = roleRepo;
        this.userMapper = userMapper;
    }

    // 📊 Получение общей статистики
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return Map.of(
                "users", userRepo.count(),
                "events", eventRepo.count(),
                "registrations", regRepo.count()
        );
    }

    // 📋 Получение списка всех пользователей
    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    // 🔄 Смена роли пользователя
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody UpdateUserRoleRequest request) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Role> optionalRole = roleRepo.findByName(request.getRole().toUpperCase());
        if (optionalRole.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        User user = optionalUser.get();
        user.setRoles(new HashSet<>(List.of(optionalRole.get())));
        userRepo.save(user);
        return ResponseEntity.ok().build();
    }


    // 🚫 Блокировка/разблокировка пользователя
    @PatchMapping("/users/{id}/block")
    public ResponseEntity<?> toggleBlockUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setBlocked(!user.isBlocked());
        userRepo.save(user);
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "blocked", user.isBlocked()
        ));
    }
}
