package com.example.demo.dto;

import com.example.demo.entity.User;

import lombok.Data;

@Data
public class UserInfoResponseDTO {
    private String username;
    private String email;
    private String link;

    public UserInfoResponseDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.link = user.getLink();
    }
}
