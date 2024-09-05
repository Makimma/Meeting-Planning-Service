package com.example.demo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AvailableSlotResponse {
    @JsonProperty("start_time")
    private ZonedDateTime startTime;

    @JsonProperty("end_time")
    private ZonedDateTime endTime;
}
