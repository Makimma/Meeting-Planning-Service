package com.example.demo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class MeetingTypeTimeRangeRequest {
    @NotNull(message = "День недели не может быть пустым")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Время начала не может быть пустым")
    private LocalTime startTime;

    @NotNull(message = "Время окончания не может быть пустым")
    private LocalTime endTime;
}
