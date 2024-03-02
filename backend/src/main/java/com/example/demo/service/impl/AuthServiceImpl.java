package com.example.demo.service.impl;

import com.example.demo.dto.JwtRequestDTO;
import com.example.demo.dto.JwtResponseDTO;
import com.example.demo.dto.UserRegistrationDTO;
import com.example.demo.entity.user.User;
import com.example.demo.exception.AppError;
import com.example.demo.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserService userService,
                           JwtTokenUtils jwtTokenUtils,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<?> createNewUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDTO.getEmail());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "Пользователь с таким адресом электронной почты уже существует"),
                    HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setUsername(userRegistrationDTO.getUsername());
        userService.save(user);
        return ResponseEntity.ok("Пользователь успешно создан");
    }

    public ResponseEntity<?> createAuthToken(JwtRequestDTO jwtRequestDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequestDTO.getEmail(), jwtRequestDTO.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(jwtRequestDTO.getEmail());
        String token = jwtTokenUtils.generateToken(userDetails, jwtRequestDTO.getEmail());
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }
}
