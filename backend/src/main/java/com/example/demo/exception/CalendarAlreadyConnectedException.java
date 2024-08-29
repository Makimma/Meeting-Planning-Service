package com.example.demo.exception;

public class CalendarAlreadyConnectedException extends RuntimeException {
    public CalendarAlreadyConnectedException(String message) {
        super(message);
    }
}
