package com.tornado.chatbot.services;

import com.tornado.chatbot.repositories.ChatSessionRepository;
import com.tornado.chatbot.utils.TitleGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @InjectMocks
    private ChatSessionService chatSessionService;

    @Test
    @DisplayName("Should create session with provided title")
    void shouldCreateSessionWithProvidedTitle() {
        // Given
        String title = "My Chat Session";
        String expectedSessionId = "session-123";
        when(chatSessionRepository.createSession(title)).thenReturn(expectedSessionId);

        // When
        String sessionId = chatSessionService.createSession(title);

        // Then
        assertThat(sessionId).isEqualTo(expectedSessionId);
        verify(chatSessionRepository).createSession(title);
    }

    @Test
    @DisplayName("Should generate title when title is null")
    void shouldGenerateTitleWhenTitleIsNull() {
        // Given
        String generatedTitle = "Generated Title";
        String expectedSessionId = "session-456";

        try (MockedStatic<TitleGenerator> titleGeneratorMock = mockStatic(TitleGenerator.class)) {
            titleGeneratorMock.when(TitleGenerator::generate).thenReturn(generatedTitle);
            when(chatSessionRepository.createSession(generatedTitle)).thenReturn(expectedSessionId);

            // When
            String result = chatSessionService.createSession(null);

            // Then
            assertThat(result).isEqualTo(expectedSessionId);
            titleGeneratorMock.verify(TitleGenerator::generate);
            verify(chatSessionRepository).createSession(generatedTitle);
        }
    }

    @Test
    @DisplayName("Should propagate exception from repository")
    void shouldPropagateExceptionFromRepository() {
        // Given
        String title = "Test Session";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(chatSessionRepository.createSession(title)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> chatSessionService.createSession(title))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(chatSessionRepository).createSession(title);
    }
}
