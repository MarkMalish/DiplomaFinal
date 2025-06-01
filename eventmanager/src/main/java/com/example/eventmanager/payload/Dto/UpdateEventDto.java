package com.example.eventmanager.payload.Dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class UpdateEventDto {

        @NotBlank(message = "Title is required")
        private String title;

        @NotNull(message = "Date is required")
        @Future(message = "Date must be in the future")
        private LocalDateTime date;

        private String description;

        @NotNull(message = "Category ID is required")
        private Long categoryId;
    private boolean canceled;

    @NotNull(message = "Max participants is required")
    @Min(1)
    private Integer maxParticipants;

        // геттеры и сеттеры
        public boolean isCanceled() {
            return canceled;
        }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }


    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}

