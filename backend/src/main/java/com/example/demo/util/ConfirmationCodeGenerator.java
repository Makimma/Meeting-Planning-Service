package com.example.demo.util;

import java.security.SecureRandom;

public class ConfirmationCodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken() {
        int number = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}
