package com.tornado.chatbot;

import com.tornado.chatbot.api.ChatController;
import com.tornado.chatbot.api.dto.MessageRequest;
import com.tornado.chatbot.api.dto.MessageResponse;
import com.tornado.chatbot.exception.ChatbotException;
import com.tornado.chatbot.services.ChatMessageService;
import com.tornado.chatbot.services.ChatSessionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatController Unit Tests")
class ChatControllerTest {
    @Mock
    private ChatSessionService chatSessionService;

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private ChatController chatController;

    @Test
    @DisplayName("Should create session when title is null")
    void shouldCreateSessionWhenTitleIsNull() {
        // Given
        String expectedSessionId = "session-456";
        when(chatSessionService.createSession(null)).thenReturn(expectedSessionId);

        // When
        ResponseEntity<String> result = chatController.createSession(null);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(   HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(expectedSessionId);
        verify(chatSessionService).createSession(null);
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when prompt request is null")
    void shouldReturnBadRequestWhenMessageRequestIsNull() {
        // Given
        MessageRequest request = null;
        boolean forceCreateSession = false;

        // When
        ResponseEntity<MessageResponse> result = chatController.createMessage(request, forceCreateSession);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNull();

        verifyNoInteractions(chatSessionService, chatMessageService);
    }
}
