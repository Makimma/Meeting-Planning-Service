package com.example.demo.service.impl;

import com.example.demo.entity.ConfirmationCode;
import com.example.demo.entity.User;
import com.example.demo.repository.ConfirmationCodeRepository;
import com.example.demo.service.ConfirmationCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {
    private final ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    public ConfirmationCodeServiceImpl(ConfirmationCodeRepository confirmationCodeRepository) {
        this.confirmationCodeRepository = confirmationCodeRepository;
    }

    @Override
    public Optional<ConfirmationCode> findFirstByUserOrderByIdDesc(User user) {
        return confirmationCodeRepository.findFirstByUserOrderByIdDesc(user);
    }

    @Override
    public ConfirmationCode save(ConfirmationCode token) {
        return confirmationCodeRepository.save(token);
    }
}
