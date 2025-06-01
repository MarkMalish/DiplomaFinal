package com.example.eventmanager.controller.auth;

import com.example.eventmanager.model.Role;
import com.example.eventmanager.model.User;
import com.example.eventmanager.repository.RoleRepository;
import com.example.eventmanager.repository.UserRepository;
import com.example.eventmanager.security.jwt.JwtUtils;
import com.example.eventmanager.service.UserService;
import com.example.eventmanager.payload.LoginRequest;
import com.example.eventmanager.payload.SignupRequest;
import com.example.eventmanager.payload.JwtResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleRepository roleRepo;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            RoleRepository roleRepo,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleRepo = roleRepo;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String login = request.getLogin();
        String password = request.getPassword();

        // Попробуем найти пользователя по username
        Optional<User> userOpt = userService.findByUsername(login);

        // Если не нашли — пробуем по email
        if (userOpt.isEmpty()) {
            userOpt = userService.findByEmail(login);
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOpt.get();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password)
        );

        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepo.findByName("USER").orElseThrow();
        user.setRoles(Set.of(userRole));

        userService.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}


