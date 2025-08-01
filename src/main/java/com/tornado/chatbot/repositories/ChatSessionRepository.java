package com.tornado.chatbot.repositories;

import com.tornado.chatbot.models.ChatSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Slf4j
@Repository
public class ChatSessionRepository {

    private final DynamoDbTable<ChatSession> chatSessionTable;

    public ChatSessionRepository(
            final DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.tables.chat-session}") String tableName) {
        this.chatSessionTable = enhancedClient.table(tableName,
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
