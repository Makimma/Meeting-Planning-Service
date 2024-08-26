package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "meeting_participant")
public class MeetingParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @NotBlank(message = "Participant name cannot be blank")
    @Column(nullable = false)
    private String participantName;

    @NotBlank(message = "Participant email cannot be blank")
    @Email(message = "Invalid email")
    @Column(nullable = false)
    private String participantEmail;
}
