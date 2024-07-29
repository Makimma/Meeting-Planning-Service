//package com.example.demo.controller;
//
//import com.example.demo.dto.CreateMeetingFromPollDTO;
//import com.example.demo.dto.MeetingPollDTO;
//import com.example.demo.dto.TimeSlotDTO;
//import com.example.demo.dto.VoteDTO;
//import com.example.demo.service.MeetingPollService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/meeting-polls")
//public class MeetingPollController {
//    private final MeetingPollService meetingPollService;
//
//    @Autowired
//    public MeetingPollController(MeetingPollService meetingPollService) {
//        this.meetingPollService = meetingPollService;
//    }
//
//    @PostMapping
//    ResponseEntity<?> createMeetingPoll(@RequestBody MeetingPollDTO meetingPollDTO) {
//        return meetingPollService.createMeetingPoll(meetingPollDTO);
//    }
//
//    @GetMapping("/{meetingPollId}")
//    ResponseEntity<?> getMeetingPollInfo(@PathVariable Long meetingPollId) {
//        return meetingPollService.getMeetingPollInfo(meetingPollId);
//    }
//
//    @GetMapping
//    ResponseEntity<?> getAllMeetingPolls() {
//        return meetingPollService.getAllMeetingPolls();
//    }
//
//    @DeleteMapping("/{meetingPollId}")
//    ResponseEntity<?> deleteMeetingPoll(@PathVariable Long meetingPollId) {
//        return meetingPollService.deleteMeetingPoll(meetingPollId);
//    }
//
//    @PostMapping("/{userLink}/{meetingPollId}/vote")
//    public ResponseEntity<?> castVote(
//            @PathVariable String userLink,
//            @PathVariable Long meetingPollId,
//            @RequestBody VoteDTO voteDTO) {
//        return meetingPollService.castVote(userLink, meetingPollId, voteDTO);
//    }
//
//    @GetMapping("/{meetingPollId}/time-slots")
//    public ResponseEntity<List<TimeSlotDTO>> getTimeSlotsForPoll(@PathVariable Long meetingPollId) {
//        List<TimeSlotDTO> timeSlots = meetingPollService.getMeetingPollTimeSlots(meetingPollId);
//        return ResponseEntity.ok(timeSlots);
//    }
//
//    @GetMapping("/{meetingPollId}/votes/results")
//    public ResponseEntity<?> getTimeSlotVoteCounts(@PathVariable Long meetingPollId) {
//        return ResponseEntity.ok(meetingPollService.getMeetingPollResults(meetingPollId));
//    }
//
//    @PostMapping("/create-from-poll")
//    public ResponseEntity<?> createMeetingFromPoll(@RequestBody CreateMeetingFromPollDTO createMeetingFromPollDTO) {
//        return meetingPollService.createMeetingFromPoll(createMeetingFromPollDTO);
//    }
//}
