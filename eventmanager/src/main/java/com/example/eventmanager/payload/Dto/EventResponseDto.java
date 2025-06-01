package com.example.eventmanager.payload.Dto;

import java.time.LocalDateTime;

public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String categoryName;
    private String organizerUsername;
    private boolean canceled;
    private String imageFilename;
    private String calendarUrl;
    private boolean archived;

    private int maxParticipants;
    // геттеры и сеттеры
    public String getCalendarUrl() {
        return calendarUrl;
    }
    public void setCalendarUrl(String calendarUrl) {
        this.calendarUrl = calendarUrl;
    }
    public boolean isCanceled() {
        return canceled;
    }
    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOrganizerUsername() {
        return organizerUsername;
    }

    public void setOrganizerUsername(String organizerUsername) {
        this.organizerUsername = organizerUsername;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }



}