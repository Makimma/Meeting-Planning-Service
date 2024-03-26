package com.example.demo.repository;

import com.example.demo.entity.MeetingPollTimeSlot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingPollTimeSlotRepository extends JpaRepository<MeetingPollTimeSlot, Long> {
    List<MeetingPollTimeSlot> findByMeetingPollId(Long id);
    Optional<MeetingPollTimeSlot> findById(Long id);

    Optional<MeetingPollTimeSlot> findByIdAndMeetingPollId(Long timeSlot, Long meetingPollId);
}
