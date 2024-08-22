package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "meeting")
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private ZonedDateTime beginAt;

    @NotNull
    private ZonedDateTime endAt;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private List<MeetingPollParticipant> participants;
}
