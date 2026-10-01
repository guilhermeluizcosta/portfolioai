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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/v1/chat")
@Tag(name = "Chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Post
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Operation(
            summary = "Ask the resume assistant",
            description = "Submits a question about the portfolio owner's resume and returns a grounded answer."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Answer generated successfully",
            content = @Content(schema = @Schema(implementation = ChatResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid or oversized question",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Processing failure",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public HttpResponse<Object> chat(@Body ChatRequest request) {

        if (request.question() == null || request.question().isBlank()) {
            return HttpResponse.badRequest(new ErrorResponse("Question must not be blank"));
        }

        if (request.question().length() > ChatRequest.MAX_QUESTION_LENGTH) {
            return HttpResponse.badRequest(new ErrorResponse("Question exceeds maximum length"));
        }

        try {
            ChatResponse response = chatService.processChat(request);
            return HttpResponse.ok(response);
        } catch (RuntimeException _) {
            return HttpResponse.serverError(new ErrorResponse("Unable to process your question"));
        }
    }

}