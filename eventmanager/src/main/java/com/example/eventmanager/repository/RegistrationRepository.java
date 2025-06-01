package com.example.eventmanager.repository;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Registration;
import com.example.eventmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration,Long> {
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByUser(User user); // 🔍 Найти все регистрации пользователя

    Optional<Registration> findByUserAndEvent(User user, Event event); // 🔍 Проверить, зарегистрирован ли пользователь

    List<Registration> findByEvent(Event event); // 🔍 Найти всех пользователей на событии

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.id = :eventId")
    int countByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    long countByEvent(Event event);
    boolean existsByUserAndEvent(User user, Event event);



}
