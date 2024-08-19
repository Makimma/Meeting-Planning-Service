package com.example.demo.response;

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
    private Long locationId;
    private String locationName;
    private String creatorName;
    private List<TimeSlotResponse> timeSlots;
}
