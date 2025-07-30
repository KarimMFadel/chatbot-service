package com.tornado.chatbot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@EqualsAndHashCode
@Setter
@Builder
@DynamoDbBean
@AllArgsConstructor
public class ChatMessage {
    private String sessionId;
    private String messageId;
    private String prompt;
    private String content;
    private Long timestamp;

    public ChatMessage() {} // Default constructor for DynamoDB

    @DynamoDbPartitionKey
    @DynamoDbAttribute("sessionId")
    public String getSessionId() {
        return sessionId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("messageId")
    public String getMessageId() {
        return messageId;
    }

    public String getPrompt() {
        return prompt;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}
