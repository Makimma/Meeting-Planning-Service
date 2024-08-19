package com.example.demo.repository;

import com.example.demo.entity.MeetingPoll;
import com.example.demo.entity.MeetingPollParticipant;
import com.example.demo.entity.MeetingPollTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingPollParticipantRepository extends JpaRepository<MeetingPollParticipant, Long> {
    boolean existsByMeetingPollAndParticipantEmail(MeetingPoll meetingPoll, String participantEmail);

    List<MeetingPollParticipant> findAllBySelectedTimeSlotsContaining(MeetingPollTimeSlot timeSlot);
}
