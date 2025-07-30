package com.tornado.chatbot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@EqualsAndHashCode
@Setter
@Builder
@DynamoDbBean
@AllArgsConstructor
public class ChatSession {
    private String sessionId;
    private String title;
    private Long createdAt;
    private Long updatedAt;

    public ChatSession() {} // Default constructor for DynamoDB

    @DynamoDbPartitionKey
    public String getSessionId() {
        return sessionId;
    }

    public String getTitle() {
        return title;
    }

    @DynamoDbAttribute("created_at")
    public Long getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute("updated_at")
    public Long getUpdatedAt() {
        return updatedAt;
    }
}
