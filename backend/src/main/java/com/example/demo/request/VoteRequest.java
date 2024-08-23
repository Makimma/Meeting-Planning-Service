package com.example.demo.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VoteRequest {
    @JsonProperty("participant_name")
    @NotBlank(message = "Participant name is required")
    private String participantName;

    @JsonProperty("participant_email")
    @NotBlank(message = "Participant email is required")
    @Email(message = "Invalid email format")
    private String participantEmail;

    @JsonProperty("selected_time_slot_ids")
    @NotNull(message = "Selected time slots are required")
    @Size(min = 1, message = "At least one time slot must be selected")
    private List<Long> selectedTimeSlotIds;

}