package com.tornado.chatbot.api.dto;

public record MessageResponse(
    String messageId,
    String sessionId,
    String content,
    String prompt,
    String timestamp) {
}
