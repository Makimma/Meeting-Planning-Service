package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "meeting")
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 32, message = "Title must not exceed 32 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Begin time cannot be null")
    private ZonedDateTime beginAt;

    @NotNull(message = "End time cannot be null")
    private ZonedDateTime endAt;

    //TODO Добавить ссылку на ивент

    @OneToMany(fetch = FetchType.LAZY)
    private List<MeetingParticipant> participants;
}
