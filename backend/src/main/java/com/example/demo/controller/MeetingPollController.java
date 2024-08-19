package com.example.demo.controller;

import com.example.demo.dto.CreateMeetingFromPollDTO;
import com.example.demo.response.VoteCountResponse;
import com.example.demo.request.MeetingPollRequest;
import com.example.demo.request.VoteRequest;
import com.example.demo.response.MeetingPollResponse;
import com.example.demo.service.MeetingPollService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/meeting-polls")
public class MeetingPollController {
    private final MeetingPollService meetingPollService;

    @Autowired
    public MeetingPollController(MeetingPollService meetingPollService) {
        this.meetingPollService = meetingPollService;
    }

    @PostMapping
    ResponseEntity<MeetingPollResponse> createMeetingPoll(@Valid @RequestBody MeetingPollRequest meetingPollRequest) {
        return ResponseEntity.ok(meetingPollService.createMeetingPoll(
                meetingPollRequest.getTitle(),
                meetingPollRequest.getDescription(),
                meetingPollRequest.getDuration(),
                meetingPollRequest.getLocationId(),
                meetingPollRequest.getTimeSlots()));
    }

    @GetMapping("/{meetingPollId}")
    ResponseEntity<MeetingPollResponse> getMeetingPollInfo(@PathVariable Long meetingPollId) {
        return ResponseEntity.ok(meetingPollService.getMeetingPollInfo(meetingPollId));
    }

    @GetMapping
    ResponseEntity<List<MeetingPollResponse>> getAllMeetingPolls() {
        return ResponseEntity.ok(meetingPollService.getMeetingPollsByUser());
    }

    @GetMapping("/{userLink}/{meetingPollId}")
    public ResponseEntity<MeetingPollResponse> getMeetingPollByUserLinkAndId(
            @PathVariable String userLink,
            @PathVariable Long meetingPollId) {

        MeetingPollResponse meetingPollResponse = meetingPollService.getMeetingPollByUserLinkAndId(userLink, meetingPollId);
        return ResponseEntity.ok(meetingPollResponse);
    }

    @PostMapping("/{userLink}/{meetingPollId}/vote")
    public ResponseEntity<Map<String, String>> vote(@PathVariable String userLink, @PathVariable Long meetingPollId, @Valid @RequestBody VoteRequest voteRequest) {
        meetingPollService.vote(userLink, meetingPollId, voteRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Vote recorded successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{meetingPollId}")
    ResponseEntity<Void> deleteMeetingPoll(@PathVariable Long meetingPollId) {
        meetingPollService.deleteMeetingPoll(meetingPollId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{meetingPollId}/votes/results")
    public ResponseEntity<List<VoteCountResponse>> getTimeSlotVoteCounts(@PathVariable Long meetingPollId) {
        return ResponseEntity.ok(meetingPollService.getVoteCountsForMeetingPoll(meetingPollId));
    }

    @PostMapping("/create-from-poll")
    public ResponseEntity<?> createMeetingFromPoll(@RequestBody CreateMeetingFromPollDTO createMeetingFromPollDTO) {
        return meetingPollService.createMeetingFromPoll(createMeetingFromPollDTO);
    }
}
