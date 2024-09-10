package com.example.demo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationRequest {
    @NotNull
    private Long id;

    private String address;

    private Boolean delete;
}
