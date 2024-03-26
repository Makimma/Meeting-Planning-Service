package com.example.demo.entity;

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
@Table(name = "meeting_poll_vote")
@AllArgsConstructor
@NoArgsConstructor
public class MeetingPollVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    private MeetingPollTimeSlot meetingPollTimeSlot;

    @Column(nullable = false)
    private String registeredName;

    @Column(nullable = false)
    private String registeredEmail;

    public MeetingPollVote(MeetingPollTimeSlot meetingPollTimeSlot,
                           String registeredName,
                           String registeredEmail) {
        this.meetingPollTimeSlot = meetingPollTimeSlot;
        this.registeredName = registeredName;
        this.registeredEmail = registeredEmail;
    }
}
