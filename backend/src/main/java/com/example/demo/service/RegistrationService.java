package com.example.demo.service;

import com.example.demo.response.ConfirmationUserResponse;
import com.example.demo.response.RegistrationResponse;
import com.example.demo.response.ResendCodeResponse;
import com.example.demo.response.SendConfirmationResponse;
import org.springframework.stereotype.Service;

@Service
public interface RegistrationService {
    RegistrationResponse createNewUser(String username, String email, String password);
    ResendCodeResponse resendCode(String email);
    ConfirmationUserResponse confirmCode(String email, String code);
    SendConfirmationResponse sendCode(String email);
}
