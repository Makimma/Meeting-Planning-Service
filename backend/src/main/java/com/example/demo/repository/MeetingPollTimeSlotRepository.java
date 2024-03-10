package com.example.demo.repository;

import com.example.demo.entity.MeetingPollTimeSlot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingPollTimeSlotRepository extends JpaRepository<MeetingPollTimeSlot, Long> {
    //TODO:
}
