package com.example.demo.response;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class MeetingResponse {
    private Long id;
    private String title;
    private String description;
    private ZonedDateTime beginAt;
    private ZonedDateTime endAt;
    private List<ParticipantResponse> participantNames;
}
