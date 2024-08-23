package com.example.demo.service;

import com.example.demo.response.MeetingResponse;
import com.example.demo.response.VoteCountResponse;
import com.example.demo.request.TimeSlotRequest;
import com.example.demo.request.VoteRequest;

import com.example.demo.response.MeetingPollResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MeetingPollService {
    MeetingPollResponse createMeetingPoll(String title, String description, int duration, Long locationId, List<TimeSlotRequest> timeSlotRequests);

    MeetingPollResponse getMeetingPollInfo(Long meetingPollId);

    List<MeetingPollResponse> getMeetingPollsByUser();

    void vote(String userLink, Long meetingPollId, VoteRequest voteRequest);

    MeetingPollResponse getMeetingPollByUserLinkAndId(String userLink, Long meetingPollId);

    void deleteMeetingPoll(Long meetingPollId);

    List<VoteCountResponse> getVoteCountsForMeetingPoll(Long meetingPollId);

    MeetingResponse createMeetingFromPoll(Long meetingPollId, Long timeSlotId);

    //TODO изменить встречу
}
