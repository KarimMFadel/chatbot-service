package com.tornado.chatbot.repositories;

import java.util.List;

import com.tornado.chatbot.models.ChatMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Slf4j
@Repository
public class ChatMessageRepository {

    private final DynamoDbTable<ChatMessage> chatMessageTable;

    public ChatMessageRepository(
            final DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.tables.chat-message}") String tableName) {
        this.chatMessageTable = enhancedClient.table(tableName,
                TableSchema.fromBean(ChatMessage.class));
    }

    public ChatMessage createMessage(final String prompt, final String content, final String sessionId) {
        String messageId = java.util.UUID.randomUUID().toString();
        ChatMessage message = ChatMessage.builder()
                .sessionId(sessionId)
                .messageId(messageId)
                .prompt(prompt)
                .content(content)
                .timestamp(System.currentTimeMillis())
                .build();

        chatMessageTable.putItem(message);

        return message;
    }

    public ChatMessage getChatMessage(String sessionId, String messageId) {
        return chatMessageTable.getItem(
                Key.builder()
                        .partitionValue(sessionId)
                        .sortValue(messageId)
                        .build());
    }

    public List<ChatMessage> getChatHistory(final String sessionId) {
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(sessionId)
                        .build()))
                .build();

        return chatMessageTable.query(query).items().stream().toList();
    }


    public List<ChatMessage> getMessagesForSession(String sessionId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(sessionId).build());

        return chatMessageTable.query(queryConditional)
                .items()
                .stream()
                .toList();
    }

}
