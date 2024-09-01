package com.example.demo.service;

import com.example.demo.entity.MeetingPoll;
import com.example.demo.response.MeetingResponse;
import com.example.demo.response.VoteCountResponse;
import com.example.demo.request.TimeSlotRequest;
import com.example.demo.request.VoteRequest;

import com.example.demo.response.MeetingPollResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MeetingPollService {
    MeetingPoll findById(Long id);
    MeetingPollResponse createMeetingPoll(String title, String description, int duration, Long locationId, List<TimeSlotRequest> timeSlotRequests);
    MeetingPollResponse getMeetingPollInfo(Long meetingPollId);
    MeetingPollResponse getMeetingPollByUserLinkAndId(String userLink, Long meetingPollId);
    //TODO изменить опрос

    MeetingResponse createMeetingFromPoll(Long meetingPollId, Long timeSlotId);

    List<MeetingPollResponse> getMeetingPollsByUser();
    List<VoteCountResponse> getVoteCountsForMeetingPoll(Long meetingPollId);

    void vote(String userLink, Long meetingPollId, VoteRequest voteRequest);
    void deleteMeetingPoll(Long meetingPollId);

}
