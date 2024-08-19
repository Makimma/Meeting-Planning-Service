package com.example.demo.entity;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "meeting_poll_time_slot")
public class MeetingPollTimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meeting_poll_id", nullable = false)
    private MeetingPoll meetingPoll;

    @Column(nullable = false)
    private ZonedDateTime beginAt;

    @Column(nullable = false)
    private ZonedDateTime endAt;

    @ManyToMany(mappedBy = "selectedTimeSlots")
    private List<MeetingPollParticipant> participants;
}
