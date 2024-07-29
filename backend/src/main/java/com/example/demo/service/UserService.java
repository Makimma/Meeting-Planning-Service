package com.example.demo.service;

import com.example.demo.request.UserUpdateRequest;
import com.example.demo.entity.User;

import com.example.demo.response.UserInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndEnabledIsTrue(String email);

    boolean existsByEmailAndEnabledIsTrue(String email);

    User save(User user);

    void deleteById(Long id);

    UserInfoResponse getUserInfo();

    UserInfoResponse updateUserInfo(UserUpdateRequest userUpdateRequest);

    Optional<User> findByLink(String userLink);
}
