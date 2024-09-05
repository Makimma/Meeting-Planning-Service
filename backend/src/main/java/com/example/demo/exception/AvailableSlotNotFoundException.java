package com.example.demo.exception;

public class AvailableSlotNotFoundException extends RuntimeException {
    public AvailableSlotNotFoundException(String message) {
        super(message);
    }
}
