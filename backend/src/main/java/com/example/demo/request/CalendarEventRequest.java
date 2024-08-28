package com.example.demo.request;

import com.example.demo.entity.MeetingParticipant;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class CalendarEventRequest {
    private String title;
    private String description;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private List<MeetingParticipant> participants;
    private String location;
    private String link;
}
