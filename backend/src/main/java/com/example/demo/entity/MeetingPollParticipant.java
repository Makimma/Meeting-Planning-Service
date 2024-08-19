package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meeting_poll_participant")
public class MeetingPollParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_event_id", nullable = false)
    private MeetingPoll meetingPoll;

    @Column(nullable = false)
    private String participantName;

    @Column(nullable = false, unique = true)
    private String participantEmail;

    @ManyToMany
    @JoinTable(
            name = "participant_time_slots",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "time_slot_id")
    )
    private List<MeetingPollTimeSlot> selectedTimeSlots;
}
