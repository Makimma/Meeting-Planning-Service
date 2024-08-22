package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Token cannot be blank")
    @Column(nullable = false, unique = true)
    private String token;

    @NotNull(message = "Creation time cannot be null")
    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @NotNull(message = "Expiration time cannot be null")
    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    @NotNull(message = "User cannot be null")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean revoked = false;
}
