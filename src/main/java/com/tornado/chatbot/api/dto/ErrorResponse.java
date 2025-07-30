package com.tornado.chatbot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorResponse {
    private String errorCode;
    private String message;
    private String timestamp;

    public ErrorResponse() {
        this.timestamp = java.time.Instant.now().toString();
    }

    public ErrorResponse(String errorCode, String message) {
        this();
        this.errorCode = errorCode;
        this.message = message;
    }
}
