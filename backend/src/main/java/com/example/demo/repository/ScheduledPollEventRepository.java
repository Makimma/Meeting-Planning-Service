package com.example.demo.repository;

import com.example.demo.entity.Meeting;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledPollEventRepository extends JpaRepository<Meeting, Long> {
}
