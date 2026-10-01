package com.portfolioai.exception;

public class ChatProcessingException extends RuntimeException {

    public ChatProcessingException() {
        super("Unable to process your question");
    }
}