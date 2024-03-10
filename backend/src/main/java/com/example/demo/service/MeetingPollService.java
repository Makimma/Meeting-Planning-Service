package com.example.demo.service;

import com.example.demo.dto.MeetingPollDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MeetingPollService {

    ResponseEntity<?> createMeetingPoll(MeetingPollDTO meetingPollDTO);
    ResponseEntity<?> getMeetingPollInfo(Long meetingPollId);
    ResponseEntity<?> getAllMeetingPoll();
}
