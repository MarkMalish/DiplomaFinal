package com.example.eventmanager.controller;

import com.example.eventmanager.mapper.RegistrationMapper;
import com.example.eventmanager.mapper.UserMapper;
import com.example.eventmanager.model.Registration;
import com.example.eventmanager.model.User;
import com.example.eventmanager.payload.Dto.RegistrationResponseDto;
import com.example.eventmanager.payload.Dto.UserDto;
import com.example.eventmanager.security.service.CustomUserDetails;
import com.example.eventmanager.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;
    private final UserMapper userMapper;


    public RegistrationController(RegistrationService registrationService,
                                  RegistrationMapper registrationMapper,
                                  UserMapper userMapper) {
        this.registrationService = registrationService;
        this.registrationMapper = registrationMapper;
        this.userMapper = userMapper;
    }

    @PostMapping("/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> register(@PathVariable Long eventId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        registrationService.register(userDetails.getUser(), eventId);
        return ResponseEntity.ok("Registered successfully");
    }

    @DeleteMapping("/me/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelOwnRegistration(@PathVariable Long eventId,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        registrationService.deleteByUserAndEvent(userDetails.getUser(), eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RegistrationResponseDto>> getMyRegistrations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var registrations = registrationService.findByUser(userDetails.getUser());
        var dtos = registrations.stream()
                .map(registrationMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<List<UserDto>> getUsersByEventId(@PathVariable Long eventId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        var users = registrationService.getUsersByEventId(eventId, userDetails.getUser());
        var dtos = users.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        userMapper.extractMainRole(user),
                        user.isBlocked()
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/event/{eventId}/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersRegisteredToEvent(@PathVariable Long eventId) {
        var users = registrationService.getUsersByEventId(eventId);
        var dtos = users.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        userMapper.extractMainRole(user),
                        user.isBlocked()
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/me/stats")
    public ResponseEntity<Map<String, Integer>> getMyRegistrationStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int total = registrationService.getTotalRegistrationsForUser(userDetails.getUser());
        return ResponseEntity.ok(Map.of("total", total));
    }
}



