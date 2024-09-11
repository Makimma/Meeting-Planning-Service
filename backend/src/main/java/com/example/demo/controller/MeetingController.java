package com.example.demo.controller;

import com.example.demo.response.MeetingResponse;
import com.example.demo.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings")
public class MeetingController {
    private final MeetingService meetingService;

    @Autowired
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeetingResponseById(id));
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> getAllMeetingsForUser() {
        return ResponseEntity.ok(meetingService.getAllUserMeetingResponses());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MeetingResponse>> getUpcomingMeetings() {
        return ResponseEntity.ok(meetingService.getUpcomingMeetings());
    }

    @GetMapping("/past")
    public ResponseEntity<List<MeetingResponse>> getPastMeetings() {
        return ResponseEntity.ok(meetingService.getPastMeetings());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
