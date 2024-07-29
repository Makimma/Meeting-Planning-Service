package com.example.demo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmationUserRequest {
    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String token;
}
