package com.example.demo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    @JsonProperty("location_id")
    private Long locationId;
}