package com.example.demo.service;



import com.example.demo.dto.AuthRequestDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    public ResponseEntity<?> createAuthToken(AuthRequestDTO authRequest);
}
