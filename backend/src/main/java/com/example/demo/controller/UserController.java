package com.example.demo.controller;

import com.example.demo.dto.UserUpdateRequestDTO;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo() {
        return userService.getUserInfo();
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return userService.updateUserInfo(userUpdateRequestDTO);
    }
}
