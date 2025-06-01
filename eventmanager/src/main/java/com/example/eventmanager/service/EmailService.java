package com.example.eventmanager.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
