package com.tornado.chatbot.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest (
    @NotBlank(message = "prompt cannot be empty or blank")
    @Size(max = 2000, message = "prompt cannot exceed 2000 characters")
    String prompt,
    String sessionId) {
}
