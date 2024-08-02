package com.example.demo.service;

import com.example.demo.entity.ConfirmationCode;

import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ConfirmationCodeService {
    ConfirmationCode save(ConfirmationCode token);
    Optional<ConfirmationCode> findFirstByUserOrderByIdDesc(User user);
}
