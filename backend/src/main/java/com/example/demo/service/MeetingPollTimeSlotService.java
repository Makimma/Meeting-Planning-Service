package com.example.demo.service;

import com.example.demo.dto.MeetingPollDTO;
import com.example.demo.dto.TimeSlotDTO;
import com.example.demo.entity.MeetingPollTimeSlot;
import com.example.demo.entity.MeetingPollVote;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MeetingPollTimeSlotService {
    ResponseEntity<?> createMeetingPollTimeSlots(MeetingPollDTO meetingPollDTO, Long userId);
    List<TimeSlotDTO> getMeetingPollTimeSlots(Long meetingPollId);

    Optional<MeetingPollTimeSlot> findById(Long timeSlot);

    Optional<MeetingPollTimeSlot> findByIdAndMeetingPollId(Long timeSlot, Long meetingPollId);

    List<MeetingPollVote> findByMeetingPollIdAndRegisteredEmail(Long meetingPollId, String email);
}
