package com.tornado.chatbot.services;

import java.util.List;

import com.tornado.chatbot.api.dto.MessageResponse;
import com.tornado.chatbot.exception.ChatbotException;
import com.tornado.chatbot.models.ChatMessage;
import com.tornado.chatbot.repositories.ChatMessageRepository;
import com.tornado.chatbot.utils.DateUtils;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Slf4j
@Service
public class ChatMessageService {


    private final AnthropicAiService anthropicAiService;
    private final ChatMessageRepository repository;
    private final ChatSessionService chatSessionService;

    public ChatMessageService(
            final AnthropicAiService anthropicAiService,
            final ChatMessageRepository chatMessageRepository, final ChatSessionService chatSessionService) {
        this.anthropicAiService = anthropicAiService;
        this.repository = chatMessageRepository;
        this.chatSessionService = chatSessionService;
    }

    public MessageResponse createMessage(final String prompt, String sessionId, boolean forceCreateSession) {
        if (forceCreateSession && sessionId == null) {
            log.info("Force creating new session for prompt");
            sessionId = chatSessionService.createSession(null);
        } else {
            chatSessionService.isExistingSession(sessionId);
        }
        String content = anthropicAiService.generateContent(prompt);
        ChatMessage chatMessage = repository.createMessage(prompt, content, sessionId);
        return new MessageResponse(chatMessage.getMessageId(), sessionId,
                content, prompt, DateUtils.readableDate(chatMessage.getTimestamp()));
    }

    public List<MessageResponse> getChatHistory(final String sessionId) {
        try {
            List<ChatMessage> chatMessages = repository.getChatHistory(sessionId);
            return chatMessages.stream()
                    .map(m -> new MessageResponse(
                            m.getMessageId(),
                            m.getSessionId(),
                            m.getContent(),
                            m.getPrompt(),
                            DateUtils.readableDate(m.getTimestamp())
                    ))
                    .toList();
        } catch (ResourceNotFoundException e) {
            log.error("Chat session not found: {}", sessionId, e);
            throw new ChatbotException("CB0010", "Chat session not found: " + sessionId, e);
        } catch (DynamoDbException e) {
            log.error("Error retrieving chat messages for session: {}", sessionId, e);
            throw new ChatbotException("CB0010", "Error retrieving chat messages for session: " + sessionId, e);
        }
    }

    public List<ChatMessage> getMessagesForSession(String sessionId) {
        return repository.getMessagesForSession(sessionId);
    }

}
