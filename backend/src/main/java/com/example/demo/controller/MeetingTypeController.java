package com.example.demo.controller;

import com.example.demo.request.MeetingTypeRequest;
import com.example.demo.response.AvailableSlotResponse;
import com.example.demo.response.MeetingTypeResponse;
import com.example.demo.service.AvailableSlotService;
import com.example.demo.service.MeetingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-types")
public class MeetingTypeController {
    private final MeetingTypeService meetingTypeService;
    private final AvailableSlotService availableSlotService;

    @Autowired
    public MeetingTypeController(MeetingTypeService meetingTypeService,
                                 AvailableSlotService availableSlotService) {
        this.meetingTypeService = meetingTypeService;
        this.availableSlotService = availableSlotService;
    }

    @PostMapping
    public ResponseEntity<MeetingTypeResponse> createMeetingType(@RequestBody MeetingTypeRequest meetingTypeRequest) {
        return ResponseEntity.ok(meetingTypeService.createMeetingType(meetingTypeRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingTypeResponse> getMeetingTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingTypeService.getMeetingTypeResponseById(id));
    }

    @GetMapping
    public ResponseEntity<List<MeetingTypeResponse>> getAllMeetingTypes() {
        return ResponseEntity.ok(meetingTypeService.getAllMeetingTypesResponsesForCurrentUser());
    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlotsForMeetingType(@PathVariable Long id) {
        List<AvailableSlotResponse> availableSlots = availableSlotService.getAvailableSlotsResponse(id);
        return ResponseEntity.ok(availableSlots);
    }
}