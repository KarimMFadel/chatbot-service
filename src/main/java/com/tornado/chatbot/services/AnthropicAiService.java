package com.tornado.chatbot.services;

import java.util.List;

import com.tornado.chatbot.exception.ChatbotException;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class AnthropicAiService {
    private final AnthropicChatModel chatModel;

    public AnthropicAiService(AnthropicChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generateContent(String prompt) {
        try {
            ChatResponse response = chatModel.call(
                    new Prompt(List.of(new UserMessage(prompt)))
            );
            return response.getResult().getOutput().getText();
        } catch (Exception e) {
            throw new ChatbotException("CB0100", "Error returned from Claude AI: " + e.getMessage(), e);
        }
    }
}

