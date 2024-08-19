package com.example.demo.service;

import com.example.demo.request.MeetingPollRequest;
import com.example.demo.request.TimeSlotRequest;
import com.example.demo.entity.MeetingPollTimeSlot;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MeetingPollTimeSlotService {
    ResponseEntity<?> createMeetingPollTimeSlots(MeetingPollRequest meetingPollRequest, Long userId);
    List<TimeSlotRequest> getMeetingPollTimeSlots(Long meetingPollId);

    Optional<MeetingPollTimeSlot> findById(Long timeSlot);

    Optional<MeetingPollTimeSlot> findByIdAndMeetingPollId(Long timeSlot, Long meetingPollId);

//    List<MeetingPollVote> findByMeetingPollIdAndRegisteredEmail(Long meetingPollId, String email);
}
