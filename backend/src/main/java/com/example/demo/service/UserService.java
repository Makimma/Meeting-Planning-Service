package com.example.demo.service;

import com.example.demo.request.UpdateUserRequest;
import com.example.demo.entity.User;

import com.example.demo.response.UserInfoResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndEnabledIsTrue(String email);

    boolean existsByEmailAndIsEnabledIsTrue(String email);

    User save(User user);

    void deleteById(Long id);

    UserInfoResponse getUserInfo();

    UserInfoResponse updateUserInfo(UpdateUserRequest updateUserRequest);

    void changePassword(String newPassword);

    Optional<User> findByLink(String userLink);
}
