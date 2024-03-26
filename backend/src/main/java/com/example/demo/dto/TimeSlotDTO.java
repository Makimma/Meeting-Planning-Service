package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TimeSlotDTO {
    private Long id;
    private LocalDateTime beginAt;
    private LocalDateTime endAt;
}
