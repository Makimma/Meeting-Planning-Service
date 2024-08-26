package com.example.demo.entity;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 32, message = "Title must not exceed 32 characters")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Duration cannot be null")
    @Column(nullable = false)
    private int duration;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Creation time cannot be null")
    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @NotNull(message = "User cannot be null")
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

    private boolean active = true;
}
