package com.example.demo.service.impl;

import com.example.demo.dto.UserInfoResponseDTO;
import com.example.demo.dto.UserUpdateRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.AppError;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(),
                            "User not found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new UserInfoResponseDTO(user), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateUserInfo(UserUpdateRequestDTO userUpdateRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "User not found"),
                    HttpStatus.NOT_FOUND);
        }

        user.setUsername(userUpdateRequestDTO.getUsername());
        userRepository.save(user);

        return new ResponseEntity<>(new UserInfoResponseDTO(user), HttpStatus.OK);
    }

    @Override
    public Optional<User> findByLink(String userLink) {
        return userRepository.findByLink(userLink);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", email)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities());
    }
}
