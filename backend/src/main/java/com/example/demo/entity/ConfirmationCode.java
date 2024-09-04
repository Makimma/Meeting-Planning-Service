package com.example.demo.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "confirmation_code")
public class ConfirmationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User cannot be null")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Code cannot be blank")
    @Column(nullable = false)
    private String code;

    @NotNull(message = "Creation time cannot be null")
    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @NotNull(message = "Expiration time cannot be null")
    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    private ZonedDateTime confirmedAt;
}

