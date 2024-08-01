package com.example.demo.service;

import com.example.demo.response.ConfirmationUserResponse;
import com.example.demo.response.RegistrationResponse;
import com.example.demo.response.ResendConfirmationResponse;
import com.example.demo.response.SendConfirmationResponse;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public interface RegistrationService {
    RegistrationResponse createNewUser(String username, String email, String password) throws MessagingException;
    ResendConfirmationResponse resendConfirmationToken(String email) throws MessagingException;
    ConfirmationUserResponse confirmToken(String email, String token);
    SendConfirmationResponse sendConfirmationCode(String email);
}
