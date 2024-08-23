package com.example.demo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPollResponse {
    private Long id;

    private String title;

    private int duration;

    private String description;

    @JsonProperty("location_id")
    private Long locationId;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("time_slots")
    private List<TimeSlotResponse> timeSlots;
}
