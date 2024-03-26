package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class MeetingPollDTO {
    private String title;
    private int duration;
    private String description;
    private Long locationId;
    private List<TimeSlotDTO> timeSlots;
    private boolean isActive = true;

    public MeetingPollDTO(
            String title,
            int duration,
            String description,
            Long locationId,
            List<TimeSlotDTO> timeSlots) {
        this.title = title;
        this.duration = duration;
        this.description = description;
        this.locationId = locationId;
        this.timeSlots = timeSlots;
    }
}
