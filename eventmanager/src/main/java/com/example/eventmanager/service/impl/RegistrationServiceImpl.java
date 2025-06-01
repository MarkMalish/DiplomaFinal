package com.example.eventmanager.service.impl;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Registration;
import com.example.eventmanager.model.User;
import com.example.eventmanager.repository.EventRepository;
import com.example.eventmanager.repository.RegistrationRepository;
import com.example.eventmanager.security.jwt.JwtUtils;
import com.example.eventmanager.service.EmailService;
import com.example.eventmanager.service.RegistrationService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationRepository registrationRepo;
    private final EventRepository eventRepo;
    private final EmailService emailService;

    private final JwtUtils jwtUtils;


    public RegistrationServiceImpl(RegistrationRepository registrationRepo, EventRepository eventRepo, JwtUtils jwtUtils,
        EmailService emailService) {
        this.registrationRepo = registrationRepo;
        this.eventRepo = eventRepo;
        this.jwtUtils = jwtUtils;
       this.emailService = emailService;
    }
    @Override
    public Registration save(Registration registration) {
        Event event = registration.getEvent();
        User user = registration.getUser();

        // 1. Событие отменено
        if (event.isCanceled()) {
            throw new IllegalStateException("You cannot register for a canceled event.");
        }

        // 2. Пользователь уже зарегистрирован
        boolean alreadyRegistered = registrationRepo.existsByUserAndEvent(user, event);
        if (alreadyRegistered) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        // 3. Превышен лимит участников
        long currentCount = registrationRepo.countByEvent(event);
        if (currentCount >= event.getMaxParticipants()) {
            throw new IllegalStateException("Maximum number of participants reached.");
        }

        // 4. Сохраняем и отправляем email
        Registration saved = registrationRepo.save(registration);
        emailService.sendEmail(
                user.getEmail(),
                "You are registered!",
                "You have successfully registered for the event: " + event.getTitle()
        );
        return saved;
    }

    @Override
    public List<Registration> findAll() {
        return registrationRepo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        registrationRepo.deleteById(id);
    }

    @Override
    public void register(User user, Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow();

        if (event.isCanceled()) {
            throw new IllegalStateException("Registration is not allowed: event is canceled");
        }

        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Registration is not allowed: event has already passed");
        }

        long currentCount = registrationRepo.countByEvent(event);
        if (currentCount >= event.getMaxParticipants()) {
            throw new IllegalStateException("This event is full. Maximum participants reached.");
        }

        boolean alreadyRegistered = registrationRepo.findByUserAndEvent(user, event).isPresent();
        if (alreadyRegistered) {
            throw new IllegalStateException("User already registered for this event");
        }

        Registration reg = new Registration();
        reg.setUser(user);
        reg.setEvent(event);
        registrationRepo.save(reg);

        emailService.sendEmail(
                user.getEmail(),
                "Registration Confirmed",
                "You have registered for event: " + event.getTitle()
       );
    }

    @Override
    public List<Registration> findByUser(User user) {
        return registrationRepo.findByUser(user);
    }

    @Override
    public void unregister(User user, Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow();
        Registration reg = registrationRepo.findByUserAndEvent(user, event)
                .orElseThrow(() -> new IllegalStateException("Registration not found"));
        registrationRepo.delete(reg);
    }

    @Override
    public List<Registration> findByEventId(Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow();
        return registrationRepo.findByEvent(event);
    }

    @Override
    public void deleteByUserAndEvent(User user, Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow();
        Registration registration = registrationRepo.findByUserAndEvent(user, event)
                .orElseThrow(() -> new IllegalStateException("Registration not found"));
        registrationRepo.delete(registration);
    }

    @Override
    public List<User> getUsersByEventId(Long eventId, User requester) {
        Event event = eventRepo.findById(eventId).orElseThrow();
        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isAdmin && !event.getOrganizer().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You are not authorized to view these registrations");
        }

        return registrationRepo.findByEvent(event)
                .stream()
                .map(Registration::getUser)
                .toList();
    }

    @Override
    public List<User> getUsersByEventId(Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow();
        return registrationRepo.findByEvent(event)
                .stream()
                .map(Registration::getUser)
                .toList();
    }
    @Override
    public int getRegistrationCountForEvent(Long eventId) {
        return registrationRepo.countByEventId(eventId);
    }
    @Override
    public int getTotalRegistrationsForUser(User user) {
        return registrationRepo.countByUserId(user.getId());
    }



}