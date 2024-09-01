package com.example.demo.service;

import com.example.demo.request.UpdateUserRequest;
import com.example.demo.entity.User;

import com.example.demo.response.UserInfoResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {
    Optional<User> getOptionalByEmail(String email);
    User findByEmail(String email);
    User findByLink(String userLink);
    User save(User user);

    void deleteById(Long id);
    void changePassword(String newPassword);
    boolean existsByEmailAndEnabledIsTrue(String email);

    UserInfoResponse getMyInfo();
    UserInfoResponse getUserInfo(Long userId);
    UserInfoResponse updateUserInfo(UpdateUserRequest updateUserRequest);


}
