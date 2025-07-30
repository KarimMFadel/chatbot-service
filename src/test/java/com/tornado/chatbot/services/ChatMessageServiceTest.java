package com.tornado.chatbot.services;

import com.tornado.chatbot.api.dto.MessageResponse;
import com.tornado.chatbot.exception.ChatbotException;
import com.tornado.chatbot.models.ChatMessage;
import com.tornado.chatbot.repositories.ChatMessageRepository;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository repository;

    @Mock
    private AnthropicAiService anthropicAiService;

    @Mock
    private ChatSessionService chatSessionService;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    @DisplayName("Should create prompt with existing session when forceCreateSession is false")
    void shouldCreateMessageWithExistingSessionWhenForceCreateSessionIsFalse() {
        // Given
        String prompt = "Hello world";
        String sessionId = "existing-session-123";
        boolean forceCreateSession = false;
        String expectedMessageId = "prompt-456";
        String content = "Generated content for prompt";
        ChatMessage chatMessage = new ChatMessage(sessionId, expectedMessageId, prompt, content, System.currentTimeMillis());

        when(anthropicAiService.generateContent(prompt)).thenReturn(content);
        doNothing().when(chatSessionService).isExistingSession(sessionId);
        when(repository.createMessage(prompt, content, sessionId)).thenReturn(chatMessage);

        // When
        MessageResponse result = chatMessageService.createMessage(prompt, sessionId, forceCreateSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.messageId()).isEqualTo(expectedMessageId);
        assertThat(result.sessionId()).isEqualTo(sessionId);

        verify(chatSessionService).isExistingSession(sessionId);
        verify(repository).createMessage(prompt, content, sessionId);
        verify(chatSessionService, never()).createSession(any());
    }

    @Test
    @DisplayName("Should force create new session when sessionId is null and forceCreateSession is true")
    void shouldForceCreateNewSessionWhenSessionIdIsNullAndForceCreateSessionIsTrue() {
        // Given
        String prompt = "Hello world";
        String sessionId = null;
        boolean forceCreateSession = true;
        String newSessionId = "new-session-789";
        String expectedMessageId = "prompt-101";
        String content = "Generated content for prompt";
        ChatMessage chatMessage = new ChatMessage(newSessionId, expectedMessageId, prompt, content, System.currentTimeMillis());

        when(anthropicAiService.generateContent(prompt)).thenReturn(content);
        when(chatSessionService.createSession(null)).thenReturn(newSessionId);
        when(repository.createMessage(prompt, content, newSessionId)).thenReturn(chatMessage);

        // When
        MessageResponse result = chatMessageService.createMessage(prompt, sessionId, forceCreateSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.messageId()).isEqualTo(expectedMessageId);
        assertThat(result.sessionId()).isEqualTo(newSessionId);

        verify(chatSessionService).createSession(null);
        verify(repository).createMessage(prompt, content, newSessionId);
        verify(chatSessionService, never()).isExistingSession(any());
    }

    @Test
    @DisplayName("Should throw ChatbotException when session validation fails")
    void shouldThrowChatbotExceptionWhenSessionValidationFails() {
        // Given
        String prompt = "Hello world";
        String sessionId = "invalid-session";
        boolean forceCreateSession = false;
        ChatbotException sessionException = new ChatbotException("CB0010", "Session does not exist: " + sessionId);

        doThrow(sessionException).when(chatSessionService).isExistingSession(sessionId);

        // When & Then
        assertThatThrownBy(() -> chatMessageService.createMessage(prompt, sessionId, forceCreateSession))
                .isInstanceOf(ChatbotException.class)
                .hasMessage("Session does not exist: " + sessionId);

        verify(chatSessionService).isExistingSession(sessionId);
        verifyNoInteractions(repository);
    }

}
