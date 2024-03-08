package com.example.demo.service;

import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public interface EmailSenderService {
    void sendEmail(String to, String subject, String body) throws MessagingException;
}
