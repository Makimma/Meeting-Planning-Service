package com.example.demo.service;

import com.example.demo.entity.ConfirmationToken;

import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ConfirmationTokenService {
    public ConfirmationToken save(ConfirmationToken token);
    public Optional<ConfirmationToken> findFirstByUserOrderByIdDesc(User user);
}
