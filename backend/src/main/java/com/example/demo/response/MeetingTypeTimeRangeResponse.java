package com.example.demo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class MeetingTypeTimeRangeResponse {
    @JsonProperty("day_of_week")
    private DayOfWeek dayOfWeek;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;
}
