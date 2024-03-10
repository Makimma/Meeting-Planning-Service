package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class MeetingPollDTO {
    private String title;
    private int duration;
    private String description;
    private int locationId;
    private List<TimeSlotDTO> timeSlots;
}
