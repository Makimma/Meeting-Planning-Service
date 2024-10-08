package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "meeting_type")
public class MeetingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotBlank
    @Size(max = 64)
    private String title;

    @Size(max = 256)
    private String description;

    @NotNull
    private int durationMinutes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "meeting_type_id")
    private List<MeetingTypeLocation> locations;

    @OneToMany(mappedBy = "meetingType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingTypeTimeRange> timeRanges;

    @NotNull
    private int maxDaysInAdvance;
}
