package com.example.demo.exception;

public class TokensNotExpiredException extends RuntimeException {
    public TokensNotExpiredException(String message) {
        super(message);
    }
}
