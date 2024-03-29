package com.example.demo.entity;

import com.example.demo.dto.MeetingPollDTO;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "meeting_poll")
@NoArgsConstructor
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
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(nullable = false)
    private boolean isActive = true;

    public MeetingPoll(String title,
                       int duration,
                       String description,
                       User user,
                       Location location) {
        this.title = title;
        this.duration = duration;
        this.description = description;
        this.user = user;
        this.location = location;
        this.createdAt = new Date();
    }

    public MeetingPoll(MeetingPollDTO meetingPollDTO) {
        this.title = meetingPollDTO.getTitle();
        this.duration = meetingPollDTO.getDuration();
        this.description = meetingPollDTO.getDescription();
        this.createdAt = new Date();
    }
}
