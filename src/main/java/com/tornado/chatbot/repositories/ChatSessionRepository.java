package com.tornado.chatbot.repositories;

import java.util.Optional;

import com.tornado.chatbot.exception.ChatbotException;
import com.tornado.chatbot.models.ChatSession;
import com.tornado.chatbot.utils.TitleGenerator;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Slf4j
@Service
public class ChatSessionRepository {

    private static String TABLE_NAME = "ChatSession";

    private final DynamoDbTable<ChatSession> chatSessionTable;

    public ChatSessionRepository(final DynamoDbEnhancedClient enhancedClient) {
        this.chatSessionTable = enhancedClient.table(TABLE_NAME,
                TableSchema.fromBean(ChatSession.class));
    }

    public String createSession(String title) {
        String sessionId = java.util.UUID.randomUUID().toString();
        ChatSession session = ChatSession.builder()
                .title(title)
                .sessionId(sessionId)
                .createdAt(System.currentTimeMillis())
                .updatedAt(System.currentTimeMillis())
                .build();

        chatSessionTable.putItem(session);

        return sessionId;
    }

    public ChatSession getChatSession(final String sessionId) {
        return chatSessionTable.getItem(r -> r.key(k -> k.partitionValue(sessionId)));
    }
}
