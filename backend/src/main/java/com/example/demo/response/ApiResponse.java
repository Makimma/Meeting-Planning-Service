package com.example.demo.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private String message;
}
