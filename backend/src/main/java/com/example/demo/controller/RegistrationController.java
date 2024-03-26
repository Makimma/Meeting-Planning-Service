package com.example.demo.controller;

import com.example.demo.dto.RegistrationRequestDTO;
import com.example.demo.service.RegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(
            RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegistrationRequestDTO registrationRequestDTO) throws MessagingException {
        return registrationService.createNewUser(registrationRequestDTO);
    }

    @GetMapping("/confirmation")
    public ResponseEntity<?> confirm(@RequestParam String token) {
        return registrationService.confirmToken(token);
    }
}
