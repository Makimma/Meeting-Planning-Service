package com.example.demo.request;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimeSlotRequest {
    @NotNull(message = "Begin time is required")
    private ZonedDateTime beginAt;

    @NotNull(message = "End time is required")
    private ZonedDateTime endAt;
}
