package com.example.demo.service.impl;

import com.example.demo.dto.JwtRequestDTO;
import com.example.demo.dto.JwtResponseDTO;
import com.example.demo.exception.AppError;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtTokenUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserService userService,
                           JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public ResponseEntity<?> createAuthToken(JwtRequestDTO jwtRequestDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequestDTO.getEmail(), jwtRequestDTO.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                    "Invalid username or password"),
                    HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userService.loadUserByUsername(jwtRequestDTO.getEmail());
        String token = jwtTokenUtils.generateToken(userDetails, jwtRequestDTO.getEmail());
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }
}
