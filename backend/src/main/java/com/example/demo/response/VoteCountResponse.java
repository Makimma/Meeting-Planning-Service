package com.example.demo.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteCountResponse {
    private Long timeSlotId;

    @JsonProperty("begin_at")
    private ZonedDateTime beginAt;

    @JsonProperty("end_at")
    private ZonedDateTime endAt;

    @JsonProperty("vote_count")
    private int voteCount;

    private List<ParticipantResponse> participants;
}

