package com.example.demo.controller;

import com.example.demo.request.BookingRequest;
import com.example.demo.request.MeetingTypeRequest;
import com.example.demo.response.AvailableSlotResponse;
import com.example.demo.response.MeetingTypeResponse;
import com.example.demo.service.AvailableSlotService;
import com.example.demo.service.MeetingTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<MeetingTypeResponse> createMeetingType(@RequestBody @Valid MeetingTypeRequest meetingTypeRequest) {
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

//    @PostMapping("/{userLink}/slots/{slotId}/book")
//    public ResponseEntity<Map<String, String>> bookSlot(@PathVariable String userLink, @PathVariable Long slotId, @RequestBody @Valid BookingRequest bookingRequest) {
//        availableSlotService.bookAvailableSlot(userLink, slotId, bookingRequest.getName(), bookingRequest.getEmail());
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Slot successfully booked");
//
//        return ResponseEntity.ok(response);
//    }
}