package com.example.demo.service;

import com.example.demo.dto.MeetingPollDTO;
import com.example.demo.dto.TimeSlotDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MeetingPollTimeSlotService {
    ResponseEntity<?> createMeetingPollTimeSlots(MeetingPollDTO meetingPollDTO, Long userId);
}
