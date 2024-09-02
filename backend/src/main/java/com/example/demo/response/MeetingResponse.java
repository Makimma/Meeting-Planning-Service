package com.example.demo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("location_id")
    private Long locationId;

    private String address;

    @JsonProperty("physical_address")
    private String physicalAddress;

    @JsonProperty("begin_at")
    private ZonedDateTime beginAt;

    @JsonProperty("end_at")
    private ZonedDateTime endAt;

    @JsonProperty("participants")
    private List<ParticipantResponse> participants;
}
