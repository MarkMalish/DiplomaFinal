package com.example.eventmanager.service;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EventService {
    Event save(Event event);
    List<Event> getAllEvents();
    Event findById(Long id, User requester); // ← обновлённый метод

    Event findById(Long id);
    void deleteById(Long id, User requester);
    Event update(Long id, Event event, User requester);
    List<Event> getEventsByOrganizer(User organizer);
    List<Event> filterEvents(Long categoryId, String search);

    Page<Event> getPaginatedEvents(Long categoryId, String search, Boolean futureOnly, Boolean canceled, Boolean archived, Pageable pageable);


    void cancelEvent(Long id, User requester);
    List<Event> getArchivedEvents();




}
