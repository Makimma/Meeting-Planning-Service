package com.example.demo.service;

import com.example.demo.request.AuthRequest;

import com.example.demo.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse createAuthToken(AuthRequest authRequest);
}
