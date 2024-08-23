package com.example.demo.request;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimeSlotRequest {
    @JsonProperty("begin_at")
    @NotNull(message = "Begin time is required")
    private ZonedDateTime beginAt;

    @JsonProperty("end_at")
    @NotNull(message = "End time is required")
    private ZonedDateTime endAt;
}
