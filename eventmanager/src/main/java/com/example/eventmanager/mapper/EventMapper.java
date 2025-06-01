package com.example.eventmanager.mapper;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.User;
import com.example.eventmanager.model.Category;
import com.example.eventmanager.payload.Dto.EventDto;
import com.example.eventmanager.payload.Dto.UpdateEventDto;
import com.example.eventmanager.payload.Dto.EventResponseDto;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponseDto toDto(Event event) {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setCategoryName(event.getCategory() != null ? event.getCategory().getName() : null);
        dto.setOrganizerUsername(event.getOrganizer() != null ? event.getOrganizer().getUsername() : null);
        dto.setCanceled(event.isCanceled());
        dto.setImageFilename(event.getImageFilename());
        dto.setArchived(event.isArchived());
        dto.setCalendarUrl("/api/events/" + event.getId() + "/calendar.ics");
        dto.setMaxParticipants(event.getMaxParticipants());
        return dto;
    }

    public Event fromDto(EventDto dto, User organizer, Category category) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setOrganizer(organizer);
        event.setCategory(category);
        event.setCanceled(dto.isCanceled());
        event.setMaxParticipants(dto.getMaxParticipants());
        return event;
    }

    public void updateEvent(Event event, UpdateEventDto dto, Category category) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setCategory(category);
        event.setCanceled(dto.isCanceled());
        event.setMaxParticipants(dto.getMaxParticipants());
    }
}
