package com.example.demo.service.impl;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtTokenUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${token.refresh.lifetime}")
    private Duration refreshLifetime;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserService userService,
                           JwtTokenUtils jwtTokenUtils,
                           RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String authenticateUser(String email, String password) {
        if (!userService.existsByEmailAndEnabledIsTrue(email)) {
            throw new UserNotFoundException("User not found");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserDetails userDetails = userService.loadUserByUsername(email);
        return jwtTokenUtils.generateToken(userDetails, email);
    }

    @Override
    public String generateRefreshToken(String email) {
        String token = jwtTokenUtils.generateRefreshToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setCreatedAt(ZonedDateTime.now());
        refreshToken.setExpiresAt(ZonedDateTime.now().plusMinutes(refreshLifetime.toMinutes()));
        refreshToken.setUser(userService.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found")));
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    @Override
    public String refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    String refreshTokenValue = cookie.getValue();
                    //TODO Exception
                    RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

                    //TODO Exception
                    if (refreshToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
                        throw new RuntimeException("Refresh token expired");
                    }

                    //TODO Exception
                    if (refreshToken.isRevoked()) {
                        throw new RuntimeException("Refresh token revoked");
                    }

                    User user = refreshToken.getUser();
                    UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

                    //TODO в контроллере уже в куки
                    //TODO Optional
                    return jwtTokenUtils.generateToken(userDetails, user.getEmail());
                }
            }
        }
        //TODO Exception
        throw new RuntimeException("Refresh token not found");
    }

}
