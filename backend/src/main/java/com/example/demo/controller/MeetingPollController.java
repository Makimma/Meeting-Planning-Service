package com.example.demo.controller;

import com.example.demo.dto.MeetingPollDTO;
import com.example.demo.service.MeetingPollService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meeting-poll")
public class MeetingPollController {

    private final MeetingPollService meetingPollService;

    @Autowired
    public MeetingPollController(MeetingPollService meetingPollService) {
        this.meetingPollService = meetingPollService;
    }

    @GetMapping("/info/{meetingPollId}") //TODO:
    ResponseEntity<?> getMeetingPollInfo(@PathVariable int meetingPollId) {
        return null;
    }

    @PostMapping("/create")
    ResponseEntity<?> createMeetingPoll(@RequestBody MeetingPollDTO meetingPollDTO) {
        return meetingPollService.createMeetingPoll(meetingPollDTO);
    }
}
