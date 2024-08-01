package com.example.demo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SendConfirmationRequest {
    @Email
    private String email;
}
