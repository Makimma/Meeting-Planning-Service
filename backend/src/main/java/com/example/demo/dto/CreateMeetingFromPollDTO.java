package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateMeetingFromPollDTO {
    private Long pollId;
    private Long timeSlotId;
    private String comment;
}
