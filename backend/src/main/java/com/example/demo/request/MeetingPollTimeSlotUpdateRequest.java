package com.example.demo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MeetingPollTimeSlotUpdateRequest {
    private Long id;

    @JsonProperty("begin_at")
    private ZonedDateTime beginAt;

    @JsonProperty("end_at")
    private ZonedDateTime endAt;

    private Boolean delete = false;
}