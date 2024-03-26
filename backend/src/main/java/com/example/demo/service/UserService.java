package com.example.demo.service;

import com.example.demo.dto.UserUpdateRequestDTO;
import com.example.demo.entity.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {
    public void enableUser(String email);

    Optional<User> findByEmail(String email);
    User save(User user);

    void deleteById(Long id);

    ResponseEntity<?> getUserInfo();

    ResponseEntity<?> updateUserInfo(UserUpdateRequestDTO userUpdateRequestDTO);

    Optional<User> findByLink(String userLink);
}
