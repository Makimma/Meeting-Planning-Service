package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Meeting poll cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_event_id", nullable = false)
    private MeetingPoll meetingPoll;

    @NotBlank(message = "Participant name cannot be blank")
    @Column(nullable = false)
    private String participantName;

    @NotBlank(message = "Participant email cannot be blank")
    @Email(message = "Invalid email")
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
