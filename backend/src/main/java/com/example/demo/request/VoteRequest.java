package com.example.demo.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VoteRequest {
    @NotBlank(message = "Participant name is required")
    private String participantName;

    @NotBlank(message = "Participant email is required")
    @Email(message = "Invalid email format")
    private String participantEmail;

    @NotNull(message = "Selected time slots are required")
    @Size(min = 1, message = "At least one time slot must be selected")
    private List<Long> selectedTimeSlotIds;

}