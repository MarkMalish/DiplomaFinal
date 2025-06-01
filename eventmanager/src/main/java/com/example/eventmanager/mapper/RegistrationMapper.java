package com.example.eventmanager.mapper;

import com.example.eventmanager.model.Registration;
import com.example.eventmanager.payload.Dto.RegistrationResponseDto;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public RegistrationResponseDto toDto(Registration reg) {
        RegistrationResponseDto dto = new RegistrationResponseDto();
        dto.setEventId(reg.getEvent().getId());
        dto.setEventTitle(reg.getEvent().getTitle());
        dto.setOrganizer(reg.getEvent().getOrganizer().getUsername());
        dto.setEventDate(reg.getEvent().getDate().toString()); // строкой или LocalDateTime
        return dto;
    }
}
