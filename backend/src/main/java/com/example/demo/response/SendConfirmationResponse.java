package com.example.demo.response;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class SendConfirmationResponse {
    private ZonedDateTime timestamp;
}
