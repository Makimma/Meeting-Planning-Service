package com.example.demo.response;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class TimeSlotResponse {
    private Long id;
    private ZonedDateTime beginAt;
    private ZonedDateTime endAt;
}
