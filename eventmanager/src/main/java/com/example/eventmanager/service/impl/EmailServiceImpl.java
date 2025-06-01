package com.example.eventmanager.service.impl;

import com.example.eventmanager.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service // ← ЭТО НУЖНО
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setFrom("ema.dev.mark@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
       message.setText(text);
        mailSender.send(message);
    }
}
