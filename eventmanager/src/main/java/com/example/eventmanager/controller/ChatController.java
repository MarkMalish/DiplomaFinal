package com.example.eventmanager.controller;

import com.example.eventmanager.payload.Dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat/{eventId}")
    @SendTo("/topic/chat/{eventId}")
    public ChatMessageDto sendMessage(@Payload ChatMessageDto message) {
        return message;
    }
}
