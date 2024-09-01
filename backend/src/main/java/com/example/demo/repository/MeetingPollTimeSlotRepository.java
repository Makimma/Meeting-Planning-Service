package com.example.demo.repository;

import com.example.demo.entity.MeetingPollTimeSlot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingPollTimeSlotRepository extends JpaRepository<MeetingPollTimeSlot, Long> {
    List<MeetingPollTimeSlot> findByIdInAndMeetingPollId(List<Long> ids, Long meetingPollId);
}
