package com.example.demo.entity;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meeting_poll")
public class MeetingPoll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int duration;

    private String description;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "meetingPoll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingPollTimeSlot> meetingPollTimeSlots;

    @OneToMany(mappedBy = "meetingPoll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingPollParticipant> meetingPollParticipants;
}
