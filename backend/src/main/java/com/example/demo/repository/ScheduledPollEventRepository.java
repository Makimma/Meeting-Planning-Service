package com.example.demo.repository;

import com.example.demo.entity.MeetingPollScheduledEvent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledPollEventRepository extends JpaRepository<MeetingPollScheduledEvent, Long> {
}
