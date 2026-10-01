package com.portfolioai.controller;

import com.portfolioai.dto.ErrorResponse;
import com.portfolioai.exception.ChatProcessingException;
import com.portfolioai.exception.InvalidQuestionException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Controller
public class ChatExceptionHandler {

    @Error(global = true, exception = InvalidQuestionException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid or oversized question",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public HttpResponse<ErrorResponse> handleInvalidQuestion(InvalidQuestionException exception) {
        return HttpResponse.badRequest(new ErrorResponse(exception.getMessage()));
    }

    @Error(global = true, exception = ChatProcessingException.class)
    @ApiResponse(
            responseCode = "500",
            description = "Processing failure",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public HttpResponse<ErrorResponse> handleChatProcessing(ChatProcessingException exception) {
        return HttpResponse.serverError(new ErrorResponse(exception.getMessage()));
    }
}
