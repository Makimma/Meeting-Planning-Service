package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class MeetingParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Participant name cannot be blank")
    @Column(nullable = false)
    private String participantName;

    @NotBlank(message = "Participant email cannot be blank")
    @Email(message = "Invalid email")
    @Column(nullable = false)
    private String participantEmail;
}
