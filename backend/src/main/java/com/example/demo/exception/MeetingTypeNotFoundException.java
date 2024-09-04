package com.example.demo.exception;

public class MeetingTypeNotFoundException extends RuntimeException {
    public MeetingTypeNotFoundException(String message) {
        super(message);
    }
}
