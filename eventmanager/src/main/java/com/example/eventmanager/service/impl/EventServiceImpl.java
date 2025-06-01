package com.example.eventmanager.service.impl;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Registration;
import com.example.eventmanager.model.User;
import com.example.eventmanager.repository.EventRepository;
import com.example.eventmanager.repository.RegistrationRepository;
import com.example.eventmanager.service.EmailService;
import com.example.eventmanager.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EmailService emailService;

    public EventServiceImpl(EventRepository eventRepository,
                            RegistrationRepository registrationRepository,
                            EmailService emailService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.emailService = emailService;
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event findById(Long id, User requester) {
        Event event = eventRepository.findById(id).orElseThrow();
        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        boolean isOrganizer = event.getOrganizer() != null &&
                event.getOrganizer().getId().equals(requester.getId());

        if (isAdmin || isOrganizer) {
            List<Registration> registrations = registrationRepository.findByEvent(event);
            event.setRegistrations(registrations);
        } else {
            event.setRegistrations(List.of()); // hide registration info
        }

        return event;
    }
    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id = " + id));
    }

    @Override
    public void deleteById(Long id, User requester) {
        Event event = eventRepository.findById(id).orElseThrow();

        // ⛔ Проверка прав
        if (!canModifyEvent(event, requester)) {
            throw new AccessDeniedException("You cannot delete this event");
        }

        List<Registration> registrations = registrationRepository.findByEvent(event);
        for (Registration reg : registrations) {
            User user = reg.getUser();
            emailService.sendEmail(
                    user.getEmail(),
                    "Event Deleted",
                   "The event '" + event.getTitle() + "' has been deleted."
            );
        }

        eventRepository.deleteById(id);
    }
    @Override
    public Event update(Long id, Event updated, User requester) {
        Event event = eventRepository.findById(id).orElseThrow();

        // ⛔ Проверка прав
        if (!canModifyEvent(event, requester)) {
            throw new AccessDeniedException("You cannot update this event");
        }

        boolean wasNotCanceledBefore = !event.isCanceled();
        boolean isNowCanceled = updated.isCanceled();

        event.setTitle(updated.getTitle());
        event.setDescription(updated.getDescription());
        event.setDate(updated.getDate());
        event.setCategory(updated.getCategory());
        event.setCanceled(updated.isCanceled());

        Event saved = eventRepository.save(event);

        if (wasNotCanceledBefore && isNowCanceled) {
            List<Registration> registrations = registrationRepository.findByEvent(saved);
            for (Registration reg : registrations) {
                User user = reg.getUser();
                emailService.sendEmail(
                        user.getEmail(),
                       "Event Canceled",
                       "The event '" + saved.getTitle() + "' has been canceled."
                );
            }
        }

        return saved;
    }
    @Override
    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }
@Override
public List<Event> filterEvents(Long categoryId, String search) {
    if (categoryId != null && search != null) {
        return eventRepository.findByCategoryIdAndTitleContainingIgnoreCase(categoryId, search);
    } else if (categoryId != null) {
        return eventRepository.findByCategoryId(categoryId);
    } else if (search != null) {
        return eventRepository.findByTitleContainingIgnoreCase(search);
    } else {
        return eventRepository.findAll();
    }
}
    @Override
    public Page<Event> getPaginatedEvents(Long categoryId, String search, Boolean futureOnly, Boolean canceled, Boolean archived, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        // 🔁 Автоархивация: один раз перед фильтрацией
        List<Event> toArchive = eventRepository.findAll().stream()
                .filter(e -> !e.isArchived() && e.getDate().isBefore(now))
                .toList();

        toArchive.forEach(e -> {
            e.setArchived(true);
            eventRepository.save(e);
        });

        // 🧾 Загружаем страницу из БД
        Page<Event> page = eventRepository.findAll(pageable);

        // 🔍 Фильтрация
        List<Event> filtered = page.getContent().stream()
                .filter(event -> {
                    if (event.isArchived()) return false;
                    if (categoryId != null && !event.getCategory().getId().equals(categoryId)) return false;
                    if (search != null && !event.getTitle().toLowerCase().contains(search.toLowerCase())) return false;
                    if (futureOnly != null && futureOnly && !event.getDate().isAfter(now)) return false;
                    if (canceled != null && event.isCanceled() != canceled) return false;
                    if (archived != null && event.isArchived() != archived) return false;
                    return true;
                })
                .toList();

        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }
    @Override
    public void cancelEvent(Long id, User requester) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // 🔒 Проверка прав на отмену
        if (!canModifyEvent(event, requester)) {
            throw new AccessDeniedException("You cannot cancel this event");
        }

        // 🔁 Если уже отменено — ничего не делаем
        if (event.isCanceled()) return;

        // 🚫 Отменяем
        event.setCanceled(true);
        eventRepository.save(event);

        // 📧 Уведомляем всех зарегистрированных пользователей
        List<Registration> registrations = registrationRepository.findByEvent(event);
        for (Registration reg : registrations) {
            User user = reg.getUser();
            if (user != null && user.getEmail() != null) {
                emailService.sendEmail(
                        user.getEmail(),
                        "Event Canceled",
                        "The event '" + event.getTitle() + "' has been canceled."
                );
            }
        }
    }
    @Override
    public List<Event> getArchivedEvents() {
        return eventRepository.findByArchivedTrue();
    }

    private boolean canModifyEvent(Event event, User user) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
        boolean isOwner = event.getOrganizer() != null &&
                event.getOrganizer().getId().equals(user.getId());
        return isAdmin || isOwner;
    }

}
