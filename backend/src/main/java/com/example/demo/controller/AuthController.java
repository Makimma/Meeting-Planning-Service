package com.example.demo.controller;

import com.example.demo.request.AuthRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.AuthResponse;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Value("${token.jwt.lifetime}")
    private Duration jwtLifetime;

    @Value("${token.refresh.lifetime}")
    private Duration refreshLifetime;

    private final AuthService authService;

    @Autowired
    public AuthController(
            AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<AuthResponse> createAuthToken(@Valid @RequestBody AuthRequest authRequest) {
        String accessToken = authService.authenticateUser(authRequest.getEmail(), authRequest.getPassword());
        String refreshToken = authService.generateRefreshToken(authRequest.getEmail());

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtLifetime.getSeconds())
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshLifetime.getSeconds())
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(AuthResponse.builder().message("Login successful").build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String newAccessToken = authService.refreshAccessToken(request, response);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body(ApiResponse.builder().message("Token refreshed").build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);

        ResponseCookie deleteAccessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie deleteRefreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefreshTokenCookie.toString())
                .body(ApiResponse.builder().message("Logout successful").build());
    }
}
