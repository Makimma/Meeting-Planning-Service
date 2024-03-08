package com.example.demo.service;

import com.example.demo.entity.User;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {
    public void enableUser(String email);

    Optional<User> findByEmail(String email);
    User save(User user);
}
