package com.example.demo.request;

import lombok.Data;

import java.util.List;

@Data
public class MeetingTypeUpdateRequest {
    private String title;

    private String description;

    private Integer durationMinutes;

    private Integer maxDaysInAdvance;

    private List<LocationRequest> locations;

    private List<MeetingTypeTimeRangeRequest> timeRanges;
}
