package com.tornado.chatbot.repositories;

import java.util.List;

import com.tornado.chatbot.exception.ChatbotException;
import com.tornado.chatbot.models.ChatMessage;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Slf4j
@Service
public class ChatMessageRepository {

    private static String TABLE_NAME = "ChatMessage";

    private final DynamoDbTable<ChatMessage> chatMessageTable;

    public ChatMessageRepository(final DynamoDbEnhancedClient enhancedClient) {
        this.chatMessageTable = enhancedClient.table(TABLE_NAME,
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
