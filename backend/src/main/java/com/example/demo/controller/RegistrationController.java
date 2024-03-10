package com.example.demo.controller;

import com.example.demo.dto.UserRegistrationDTO;
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
    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) throws MessagingException {
        return registrationService.createNewUser(userRegistrationDTO);
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam String token) {
        return registrationService.confirmToken(token);
    }
}
