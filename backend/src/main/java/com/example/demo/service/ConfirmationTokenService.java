package com.example.demo.service;

import com.example.demo.entity.ConfirmationToken;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ConfirmationTokenService {
    public void saveConfirmationToken(ConfirmationToken token);
    public Optional<ConfirmationToken> getToken(String token);

    public int deleteByUserId(Long userId);
    public int setConfirmedAt(String token);

}
