package com.tornado.chatbot.api;

import java.util.List;

import com.tornado.chatbot.api.dto.MessageRequest;
import com.tornado.chatbot.api.dto.MessageResponse;
import com.tornado.chatbot.services.ChatMessageService;
import com.tornado.chatbot.services.ChatSessionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;

    public ChatController(final ChatSessionService chatSessionService, final ChatMessageService chatMessageService) {
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("/session")
    public ResponseEntity<String> createSession(
            @RequestParam(required = false) String title) {
        log.info("Creating new chat session with title: {}", title);
        return new ResponseEntity<String>(
                chatSessionService.createSession(title), HttpStatus.CREATED);
    }

    @PostMapping("/message")
    public ResponseEntity<MessageResponse> createMessage(
            @RequestBody MessageRequest request, @RequestParam(required = false) boolean forceCreateSession) {
        log.info("Received message request: {}", request);
        if (request == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<MessageResponse>(
                chatMessageService.createMessage(request.prompt(), request.sessionId(), forceCreateSession), HttpStatus.CREATED);
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<MessageResponse>> getChatHistory(
            @PathVariable String sessionId) {
        chatSessionService.isExistingSession(sessionId);
        return new ResponseEntity<List<MessageResponse>>(
                chatMessageService.getChatHistory(sessionId), HttpStatus.OK);
    }
}
