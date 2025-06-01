package com.example.eventmanager.service;

import com.example.eventmanager.model.Registration;
import com.example.eventmanager.model.User;

import java.util.List;

public interface RegistrationService {
    Registration save(Registration registration);
    List<Registration> findAll();
    void deleteById(Long id);
    void register(User user, Long eventId);
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByUser(User user);        // ✅ посмотреть свои регистрации
    void unregister(User user, Long eventId);
    void deleteByUserAndEvent(User user, Long eventId);

    List<User> getUsersByEventId(Long eventId);
    List<User> getUsersByEventId(Long eventId, User requester);

    int getRegistrationCountForEvent(Long eventId);

    int getTotalRegistrationsForUser(User user);



}
