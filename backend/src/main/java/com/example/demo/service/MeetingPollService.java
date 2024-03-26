package com.example.demo.service;

import com.example.demo.dto.CreateMeetingFromPollDTO;
import com.example.demo.dto.MeetingPollDTO;
import com.example.demo.dto.MeetingPollResultDTO;
import com.example.demo.dto.TimeSlotDTO;
import com.example.demo.dto.VoteDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MeetingPollService {

    ResponseEntity<?> createMeetingPoll(MeetingPollDTO meetingPollDTO);
    ResponseEntity<?> getMeetingPollInfo(Long meetingPollId);

    ResponseEntity<?> deleteMeetingPoll(Long meetingPollId);

    ResponseEntity<?> getAllMeetingPolls();

    ResponseEntity<?> castVote(String userLink, Long meetingPollId, VoteDTO voteDTO);

    List<TimeSlotDTO> getMeetingPollTimeSlots(Long meetingPollId);

    List<MeetingPollResultDTO> getMeetingPollResults(Long meetingPollId);

    ResponseEntity<?> createMeetingFromPoll(CreateMeetingFromPollDTO createMeetingFromPollDTO);
}
