package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "calendar_token")
public class CalendarToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    @NotNull(message = "Calendar is required")
    private Calendar calendar;

    @NotBlank(message = "Access token cannot be blank")
    @Size(max = 512, message = "Access token must not exceed 512 characters")
    @Column(nullable = false)
    private String accessToken;

    @NotBlank(message = "Refresh token cannot be blank")
    @Size(max = 512, message = "Refresh token must not exceed 512 characters")
    @Column(nullable = false)
    private String refreshToken;

    @NotNull(message = "Expiration time is required")
    @Column(nullable = false)
    private ZonedDateTime expiresAt;
}
