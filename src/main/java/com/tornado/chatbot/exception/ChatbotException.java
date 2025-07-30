package com.tornado.chatbot.exception;

import lombok.Getter;

@Getter
public class ChatbotException extends RuntimeException {
    private final String errorCode;

    public ChatbotException(String message) {
        this("CB0000", message);
    }

    public ChatbotException(final String errorCode, String message) {
        this(errorCode, message, null);
    }

    public ChatbotException(final String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
