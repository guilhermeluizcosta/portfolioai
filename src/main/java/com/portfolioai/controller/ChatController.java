package com.portfolioai.controller;

import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import com.portfolioai.dto.ErrorResponse;
import com.portfolioai.service.ChatService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

@Controller("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Post
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<?> chat(@Body ChatRequest request) {

        if (request.question() == null || request.question().isBlank()) {
            return HttpResponse.badRequest(new ErrorResponse("Question must not be blank"));
        }

        if (request.question().length() > ChatRequest.MAX_QUESTION_LENGTH) {
            return HttpResponse.badRequest(new ErrorResponse("Question exceeds maximum length"));
        }

        try {
            ChatResponse response = chatService.processChat(request);
            return HttpResponse.ok(response);
        } catch (RuntimeException ignored) {
            return HttpResponse.serverError(new ErrorResponse("Unable to process your question"));
        }
    }

}