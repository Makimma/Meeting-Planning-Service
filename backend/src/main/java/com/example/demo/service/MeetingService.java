package com.example.demo.service;

import com.example.demo.entity.Meeting;
import com.example.demo.entity.MeetingPoll;
import com.example.demo.entity.MeetingPollTimeSlot;
import com.example.demo.response.MeetingResponse;

import java.util.List;

public interface MeetingService {
    Meeting createMeeting(MeetingPoll meetingPoll, MeetingPollTimeSlot meetingPollTimeSlot);
    void deleteMeeting(Long meetingId);
    MeetingResponse getMeetingResponseById(Long id);
    List<MeetingResponse> getAllUserMeetingResponses();
    Meeting getMeetingById(Long id);
}
