package com.example.demo.request;

import com.example.demo.entity.Location;
import lombok.Data;

import java.util.List;

@Data
public class MeetingPollUpdateRequest {
    private String title;

    private String description;

    private Location location;

    private String address;

    private List<MeetingPollTimeSlotUpdateRequest> timeSlots;
}
