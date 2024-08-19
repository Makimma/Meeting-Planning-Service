package com.example.demo.response;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class RegistrationResponse {
    private ZonedDateTime timestamp;
}
