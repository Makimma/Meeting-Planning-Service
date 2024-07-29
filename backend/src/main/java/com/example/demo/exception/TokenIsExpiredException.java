package com.example.demo.exception;

public class TokenIsExpiredException extends RuntimeException {
    public TokenIsExpiredException(String message) {
        super(message);
    }
}
