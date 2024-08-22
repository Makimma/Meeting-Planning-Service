package com.example.demo.entity;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "meeting_poll_time_slot")
public class MeetingPollTimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Meeting poll cannot be null")
    @ManyToOne
    @JoinColumn(name = "meeting_poll_id", nullable = false)
    private MeetingPoll meetingPoll;

    @NotNull(message = "Begin time is required")
    @Column(nullable = false)
    private ZonedDateTime beginAt;

    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private ZonedDateTime endAt;

    @ManyToMany(mappedBy = "selectedTimeSlots")
    private List<MeetingPollParticipant> participants;
}
