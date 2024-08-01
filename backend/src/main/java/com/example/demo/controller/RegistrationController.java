package com.example.demo.controller;

import com.example.demo.request.ConfirmationUserRequest;
import com.example.demo.request.RegistrationRequest;
import com.example.demo.request.ResendConfirmationRequest;
import com.example.demo.request.SendConfirmationRequest;
import com.example.demo.response.ConfirmationUserResponse;
import com.example.demo.response.RegistrationResponse;
import com.example.demo.response.ResendConfirmationResponse;
import com.example.demo.response.SendConfirmationResponse;
import com.example.demo.service.RegistrationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    //TODO Убрать исключение, тк обработка на уроне сервиса
    //TODO внутри вызывать отправку кода (registrationService.sendConfirmationCode)
    public ResponseEntity<RegistrationResponse> createUser(@Valid @RequestBody RegistrationRequest registrationRequest) throws MessagingException {
        return ResponseEntity.ok(registrationService.createNewUser(
                registrationRequest.getUsername(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword()));
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<ResendConfirmationResponse> resendConfirmation(@Valid @RequestBody ResendConfirmationRequest resendConfirmationRequest) throws MessagingException {
        return ResponseEntity.ok(registrationService.resendConfirmationToken(resendConfirmationRequest.getEmail()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmationUserResponse> confirm(@Valid @RequestBody ConfirmationUserRequest confirmationUserRequest) {
        return ResponseEntity.ok(registrationService.confirmToken(
                confirmationUserRequest.getEmail(),
                confirmationUserRequest.getToken()));
    }

    @PostMapping("/send-confirmation")
    public ResponseEntity<SendConfirmationResponse> sendConfirmationCode(@Valid @RequestBody SendConfirmationRequest sendConfirmationRequest) {
        return ResponseEntity.ok(registrationService.sendConfirmationCode(sendConfirmationRequest.getEmail()));
    }
}
