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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "meeting_poll_participant")
@AllArgsConstructor
@NoArgsConstructor
public class MeetingPollParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poll_event_id")
    private MeetingPoll meetingPoll;

    @Column(nullable = false)
    private String participantName;

    @Column(nullable = false)
    private String participantEmail;

    public MeetingPollParticipant(MeetingPoll meetingPoll, String participantName, String participantEmail) {
        this.meetingPoll = meetingPoll;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
    }
}
