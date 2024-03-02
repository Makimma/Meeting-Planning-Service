package com.example.demo.controller;

import com.example.demo.dto.JwtRequestDTO;
import com.example.demo.dto.UserRegistrationDTO;
import com.example.demo.entity.user.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(
            UserService userService,
            AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/hello") //test endpoint
    public String hello() {
        return "hello";
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequestDTO jwtRequestDTO) {
        return authService.createAuthToken(jwtRequestDTO);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        return authService.createNewUser(userRegistrationDTO);
    }

    @GetMapping("/users/{email}")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }
}
