package com.example.demo.service;

import com.example.demo.dto.UserRegistrationDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public interface RegistrationService {
    public ResponseEntity<?> createNewUser(UserRegistrationDTO userRegistrationDTO) throws MessagingException;
    public ResponseEntity<?> confirmToken(String token);
}
