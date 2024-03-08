package com.example.demo.entity;

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

@Data
@Entity
@Table(name = "selected_meeting_poll_time_slot")
public class SelectedMeetingPollTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meeting_poll_id")
    private MeetingPoll meetingPoll;

    @Column(nullable = false)
    private String registeredName;

    @Column(nullable = false)
    private String registeredEmail;

    @Column(nullable = false)
    private Date beginAt;

    @Column(nullable = false)
    private Date endAt;
}
