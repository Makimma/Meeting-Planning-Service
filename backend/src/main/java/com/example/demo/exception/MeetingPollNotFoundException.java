package com.example.demo.exception;

public class MeetingPollNotFoundException extends RuntimeException {
    public MeetingPollNotFoundException(String message) {
        super(message);
    }
}
