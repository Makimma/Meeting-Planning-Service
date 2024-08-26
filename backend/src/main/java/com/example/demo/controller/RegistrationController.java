package com.example.demo.controller;

import com.example.demo.request.ConfirmationUserRequest;
import com.example.demo.request.RegistrationRequest;
import com.example.demo.request.ResendCodeRequest;
import com.example.demo.request.SendConfirmationRequest;
import com.example.demo.response.ConfirmationUserResponse;
import com.example.demo.response.RegistrationResponse;
import com.example.demo.response.ResendCodeResponse;
import com.example.demo.response.SendConfirmationResponse;
import com.example.demo.service.AuthService;
import com.example.demo.service.RegistrationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {
    @Value("${token.jwt.lifetime}")
    private Duration jwtLifetime;

    @Value("${token.refresh.lifetime}")
    private Duration refreshLifetime;

    private final RegistrationService registrationService;
    private final AuthService authService;

    @Autowired
    public RegistrationController(RegistrationService registrationService,
                                  AuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
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
        ConfirmationUserResponse confirmationUserResponse = registrationService.confirmCode(
                confirmationUserRequest.getEmail(),
                confirmationUserRequest.getCode());

        String refreshToken = authService.generateRefreshToken(confirmationUserRequest.email);
        String accessToken = authService.generateAccessToken(confirmationUserRequest.email);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtLifetime.getSeconds())
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshLifetime.getSeconds())
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(confirmationUserResponse);
    }

    @PostMapping("/send-code")
    public ResponseEntity<SendConfirmationResponse> sendCode(@Valid @RequestBody SendConfirmationRequest sendConfirmationRequest) {
        return ResponseEntity.ok(registrationService.sendCode(sendConfirmationRequest.getEmail()));
    }
}
