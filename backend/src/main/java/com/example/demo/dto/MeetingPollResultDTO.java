package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeetingPollResultDTO {
    private Long timeSlotId;
    private Long voteCount;
    private LocalDateTime beginAt;
    private LocalDateTime endAt;
}
