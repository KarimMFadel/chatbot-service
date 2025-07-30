package com.tornado.chatbot.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBConfig {

    @Value("${spring.cloud.aws.dynamodb.region:us-east-1}")
    private String regionName;

    @Value("${spring.cloud.aws.dynamodb.endpoint:http://localhost:8000}")
    private String endpoint;

    @Bean
    @Profile("prod")
    public DynamoDbClient getClient() {
        Region region = Region.of(regionName);
        return DynamoDbClient.builder()
                .region(region)
                .build();
    }

    @Bean
    @Profile("dev")
    public DynamoDbClient dynamoDbClient() {
        Region region = Region.of(regionName);
        return DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpoint))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

}
