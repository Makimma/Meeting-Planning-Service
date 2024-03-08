package com.example.demo.controller;

import com.example.demo.dto.JwtRequestDTO;
import com.example.demo.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(
            AuthService authService) {
        this.authService = authService;
    }

    @PostMapping()
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequestDTO jwtRequestDTO) {
        return authService.createAuthToken(jwtRequestDTO);
    }

    @GetMapping("/hello") //test endpoint
    public String hello() {
        return "hello";
    }
}
