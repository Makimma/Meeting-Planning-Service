package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "available_slot")
public class AvailableSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_type_id", nullable = false)
    private MeetingType meetingType;

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

    private boolean reserved;

    private String name;

    private String email;
}
