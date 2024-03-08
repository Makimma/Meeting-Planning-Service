package com.example.demo.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "scheduled_poll_event")
public class ScheduledPollEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "meeting_poll_id")
    private MeetingPoll meetingPoll;

    private String comment;

    private String link;

    @Column(nullable = false)
    private Date beginAt;

    @Column(nullable = false)
    private Date endAt;
}
