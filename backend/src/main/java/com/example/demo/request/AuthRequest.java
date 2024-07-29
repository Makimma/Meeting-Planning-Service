package com.example.demo.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
