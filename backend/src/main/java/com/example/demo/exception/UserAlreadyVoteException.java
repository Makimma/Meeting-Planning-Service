package com.example.demo.exception;

public class UserAlreadyVoteException extends RuntimeException {
    public UserAlreadyVoteException(String message) {
        super(message);
    }
}
