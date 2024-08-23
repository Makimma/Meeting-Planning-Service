package com.example.demo.controller;

import com.example.demo.request.UpdatePasswordRequest;
import com.example.demo.request.UpdateUserRequest;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(userService.getMyInfo());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
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
