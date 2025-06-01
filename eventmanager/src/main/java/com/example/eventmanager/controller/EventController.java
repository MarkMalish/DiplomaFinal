package com.example.eventmanager.controller;

import com.example.eventmanager.mapper.EventMapper;
import com.example.eventmanager.model.Category;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.User;
import com.example.eventmanager.payload.Dto.EventDto;
import com.example.eventmanager.payload.Dto.EventResponseDto;
import com.example.eventmanager.payload.Dto.PagedResponseDto;
import com.example.eventmanager.repository.CategoryRepository;
import com.example.eventmanager.security.service.CustomUserDetails;
import com.example.eventmanager.service.EventService;
import com.example.eventmanager.service.RegistrationService;
import com.example.eventmanager.util.IcsGenerator;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import com.example.eventmanager.payload.Dto.UpdateEventDto;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, RegistrationService registrationService,
                           CategoryRepository categoryRepository, EventMapper eventMapper) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Event event = eventService.findById(id, userDetails.getUser());
        return ResponseEntity.ok(eventMapper.toDto(event));
    }


    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventDto eventDto,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        User organizer = userDetails.getUser();
        Category category = categoryRepository.findById(eventDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Event event = eventMapper.fromDto(eventDto, organizer, category);
        Event saved = eventService.save(event);

        return ResponseEntity.ok(eventMapper.toDto(saved));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        eventService.deleteById(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateEventDto updateDto,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        Category category = categoryRepository.findById(updateDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Event event = eventService.findById(id);
        eventMapper.updateEvent(event, updateDto, category);
        Event updated = eventService.update(id, event, currentUser);

        return ResponseEntity.ok(eventMapper.toDto(updated));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<Event>> getMyEvents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User organizer = userDetails.getUser();
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizer));
    }
    @GetMapping
    public ResponseEntity<PagedResponseDto<EventResponseDto>> getFilteredEvents(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean futureOnly,
            @RequestParam(required = false) Boolean canceled,
            @RequestParam(required = false) Boolean archived, // ← добавили
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventService.getPaginatedEvents(categoryId, search, futureOnly, canceled, archived, pageable);
        List<EventResponseDto> dtos = eventPage.getContent().stream()
                .map(eventMapper::toDto)
                .toList();

        var response = new PagedResponseDto<>(dtos, page, size, eventPage.getTotalElements(), eventPage.getTotalPages());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Integer>> getEventStats(@PathVariable Long id) {
        int count = registrationService.getRegistrationCountForEvent(id);
        Map<String, Integer> response = new HashMap<>();
        response.put("totalRegistrations", count);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/registrations/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public void exportRegistrationsToCSV(@PathVariable Long id,
                                         HttpServletResponse response) throws IOException {
        List<User> users = registrationService.getUsersByEventId(id);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=registrations_event_" + id + ".csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Username,Email");

        for (User user : users) {
            writer.println(user.getId() + "," + user.getUsername() + "," + user.getEmail());
        }

        writer.flush();
        writer.close();
    }
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportEventsToCSV(HttpServletResponse response) throws IOException {
        List<Event> events = eventService.getAllEvents();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=all_events.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Title,Date,Category,Organizer");

        for (Event event : events) {
            String category = event.getCategory() != null ? event.getCategory().getName() : "";
            String organizer = event.getOrganizer() != null ? event.getOrganizer().getUsername() : "";
            writer.println(event.getId() + "," + event.getTitle() + "," + event.getDate() + "," + category + "," + organizer);
        }

        writer.flush();
        writer.close();
    }
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        Event event = eventService.findById(id);

        // Удаляем старое изображение
        String oldFilename = event.getImageFilename();
        if (oldFilename != null) {
            Path oldPath = Paths.get("uploads/" + oldFilename);
            Files.deleteIfExists(oldPath);
        }

        // Путь к папке (например, внутри проекта)
        String uploadDir = "uploads/";
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        Path path = Paths.get(uploadDir + filename);
        Files.write(path, file.getBytes());

        // Сохраняем имя файла в событие
        event.setImageFilename(filename);
        eventService.save(event);

        return ResponseEntity.ok("Image uploaded: " + filename);
    }
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<String> cancelEvent(@PathVariable Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        eventService.cancelEvent(id, userDetails.getUser());
        return ResponseEntity.ok("Event has been canceled and participants notified.");
    }

    @GetMapping("/{id}/calendar.ics")
    public void downloadIcs(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Event event = eventService.findById(id);
        String filename = IcsGenerator.generateIcsFile(event);

        response.setContentType("text/calendar");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        Files.copy(Paths.get(filename), response.getOutputStream());
        response.getOutputStream().flush();
    }
    @GetMapping("/archived")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<List<EventResponseDto>> getArchivedEvents() {
        List<Event> archived = eventService.getArchivedEvents();
        List<EventResponseDto> dtos = archived.stream()
                .map(eventMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}
