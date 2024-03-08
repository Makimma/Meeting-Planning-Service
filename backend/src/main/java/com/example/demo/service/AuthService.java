package com.example.demo.service;



import com.example.demo.dto.JwtRequestDTO;
import com.example.demo.dto.UserRegistrationDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    public ResponseEntity<?> createAuthToken(JwtRequestDTO authRequest);
}
