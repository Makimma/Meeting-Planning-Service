package com.example.demo.exception;

public class CodeNotExpiredException extends RuntimeException {
    public CodeNotExpiredException(String message) {
        super(message);
    }
}
