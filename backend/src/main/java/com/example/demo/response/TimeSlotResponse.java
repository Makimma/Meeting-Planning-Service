package com.example.demo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class TimeSlotResponse {
    private Long id;

    @JsonProperty("begin_at")
    private ZonedDateTime beginAt;

    @JsonProperty("end_at")
    private ZonedDateTime endAt;
}
