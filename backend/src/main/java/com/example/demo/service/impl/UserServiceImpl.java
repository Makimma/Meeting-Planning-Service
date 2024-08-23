package com.example.demo.service.impl;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.request.UpdateUserRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import com.example.demo.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByEmailAndEnabledIsTrue(String email) {
        return userRepository.findByEmailAndEnabledIsTrue(email);
    }

    @Override
    public boolean existsByEmailAndEnabledIsTrue(String email) {
        return userRepository.existsByEmailAndEnabledIsTrue(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        return toUserInfoResponse(userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }

    @Override
    public UserInfoResponse getMyInfo() {
        return toUserInfoResponse(userRepository.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }

    @Override
    @Transactional
    public UserInfoResponse updateUserInfo(UpdateUserRequest updateUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        user.setUsername(updateUserRequest.getUsername());
        userRepository.save(user);

        return toUserInfoResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(String newPassword) {
        User user = userRepository.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    @Override
    public Optional<User> findByLink(String userLink) {
        return userRepository.findByLink(userLink);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities());
    }

    private UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .link(user.getLink())
                .build();
    }
}
