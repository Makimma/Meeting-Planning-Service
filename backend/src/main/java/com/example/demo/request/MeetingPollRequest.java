package com.example.demo.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPollRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 32, message = "Title must not exceed 32 characters")
    private String title;

    @NotNull(message = "Duration is required")
    private int duration;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @JsonProperty("location_id")
    @NotNull(message = "Location ID is required")
    private Long locationId;

    @JsonProperty("time_slots")
    @NotNull(message = "Time slots are required")
    @Size(min = 1, message = "At least one time slot must be provided")
    private List<@Valid TimeSlotRequest> timeSlots;
}
