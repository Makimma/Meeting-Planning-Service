package com.example.demo.controller;

import com.example.demo.request.ConfirmationUserRequest;
import com.example.demo.request.RegistrationRequest;
import com.example.demo.request.ResendCodeRequest;
import com.example.demo.request.SendConfirmationRequest;
import com.example.demo.response.ConfirmationUserResponse;
import com.example.demo.response.RegistrationResponse;
import com.example.demo.response.ResendCodeResponse;
import com.example.demo.response.SendConfirmationResponse;
import com.example.demo.service.RegistrationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> createUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(registrationService.createNewUser(
                registrationRequest.getUsername(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword()));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<ResendCodeResponse> resendCode(@Valid @RequestBody ResendCodeRequest resendCodeRequest) {
        return ResponseEntity.ok(registrationService.resendCode(resendCodeRequest.getEmail()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmationUserResponse> confirm(@Valid @RequestBody ConfirmationUserRequest confirmationUserRequest) {
        return ResponseEntity.ok(registrationService.confirmCode(
                confirmationUserRequest.getEmail(),
                confirmationUserRequest.getToken()));
    }

    @PostMapping("/send-code")
    public ResponseEntity<SendConfirmationResponse> sendCode(@Valid @RequestBody SendConfirmationRequest sendConfirmationRequest) {
        return ResponseEntity.ok(registrationService.sendCode(sendConfirmationRequest.getEmail()));
    }
}
