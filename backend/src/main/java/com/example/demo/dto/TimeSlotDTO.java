package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class TimeSlotDTO {
    private Date beginAt;
    private Date endAt;
}
