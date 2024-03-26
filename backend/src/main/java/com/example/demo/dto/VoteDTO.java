package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class VoteDTO {
    private List<Long> timeSlotId;
    private String registeredName;
    private String registeredEmail;
}