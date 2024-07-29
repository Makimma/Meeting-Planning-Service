package com.example.demo.util;

import java.security.SecureRandom;

public class ConfirmationTokenGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_LENGTH = 6;

    public static String generateToken() {
        int number = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}
