package com.example.demo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MeetingTypeResponse {
    private Long id;
    private String title;
    private String description;
    private int duration;

    @JsonProperty("max_days_in_advance")
    private int maxDaysInAdvance;

    private List<LocationResponse> locations;

    @JsonProperty("time_ranges")
    private List<MeetingTypeTimeRangeResponse> timeRanges;
}
