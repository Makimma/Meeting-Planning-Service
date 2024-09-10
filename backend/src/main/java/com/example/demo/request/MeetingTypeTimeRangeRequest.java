package com.example.demo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class MeetingTypeTimeRangeRequest {
    @NotNull(message = "Day of week can not be null")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time can not be null")
    private LocalTime startTime;

    @NotNull(message = "End time can not be null")
    private LocalTime endTime;

    private Boolean delete;
}
