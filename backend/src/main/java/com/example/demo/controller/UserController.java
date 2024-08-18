package com.example.demo.controller;

import com.example.demo.request.UpdatePasswordRequest;
import com.example.demo.request.UpdateUserRequest;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
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
    public ResponseEntity<UserInfoResponse> getMyInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PutMapping("/me")
    public ResponseEntity<UserInfoResponse> updateMyInfo(@RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(userService.updateUserInfo(updateUserRequest));
    }

    @PutMapping("/me/new-password")
    public ResponseEntity<Void> updateMyPassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        userService.changePassword(updatePasswordRequest.getPassword());
        return ResponseEntity.ok().build();
    }
}
