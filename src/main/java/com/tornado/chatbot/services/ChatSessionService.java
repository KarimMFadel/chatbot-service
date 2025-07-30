package com.tornado.chatbot.services;

import java.util.Optional;

import com.tornado.chatbot.exception.ChatbotException;
import com.tornado.chatbot.models.ChatSession;
import com.tornado.chatbot.repositories.ChatSessionRepository;
import com.tornado.chatbot.utils.TitleGenerator;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatSessionService {
    private final ChatSessionRepository chatSessionRepository;

    public ChatSessionService(final ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public String createSession(String title) {
        if (title == null || title.isBlank()) {
            title = TitleGenerator.generate();
        }
        return chatSessionRepository.createSession(title);
    }

    public ChatSession getChatSession(final String sessionId) {
        return chatSessionRepository.getChatSession(sessionId);
    }

    public void isExistingSession(final String sessionId) {
        Optional.ofNullable(getChatSession(sessionId))
                .ifPresentOrElse(
                        session -> log.debug("Session {} validated successfully", sessionId),
                        () -> {
                            log.warn("Session does not exist: {}", sessionId);
                            throw new ChatbotException("CB0010", "Session does not exist: " + sessionId);
                        }
                );
    }
}
