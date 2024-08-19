package com.example.demo.response;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteCountResponse {
    private Long timeSlotId;
    private ZonedDateTime beginAt;
    private ZonedDateTime endAt;
    private int voteCount;
    private List<ParticipantResponse> participants;
}

