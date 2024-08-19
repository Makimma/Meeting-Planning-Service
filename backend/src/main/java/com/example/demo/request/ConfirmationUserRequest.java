package com.example.demo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmationUserRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    public String email;

    @NotBlank
    @Size(min = 6, max = 6, message = "Code must be exactly 6 characters long")
    public String code;
}
