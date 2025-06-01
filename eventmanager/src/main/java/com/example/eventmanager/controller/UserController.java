package com.example.eventmanager.controller;

import com.example.eventmanager.mapper.UserMapper;
import com.example.eventmanager.model.User;
import com.example.eventmanager.payload.Dto.UserDto;
import com.example.eventmanager.repository.UserRepository;
import com.example.eventmanager.security.service.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;
    private final UserMapper userMapper;


    public UserController(UserRepository userRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }



    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepo.findAll().stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(userMapper.toDto(user));
    }

}
