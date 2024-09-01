package com.example.demo.service;

import com.example.demo.entity.Meeting;
import com.example.demo.entity.MeetingPoll;
import com.example.demo.entity.MeetingPollTimeSlot;

public interface MeetingService {
    Meeting createMeeting(MeetingPoll meetingPoll, MeetingPollTimeSlot meetingPollTimeSlot);
    void deleteMeeting(Long meetingId);
}
