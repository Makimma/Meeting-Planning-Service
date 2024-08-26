package com.example.demo.exception;

public class MeetingAlreadyExistException extends RuntimeException {
    public MeetingAlreadyExistException(String message) {
        super(message);
    }
}
