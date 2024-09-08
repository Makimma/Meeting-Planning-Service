package com.example.demo.controller;

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
@RequestMapping("/api/v1/meeting-types")
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

    @GetMapping("/{userLink}/{meetingTypeId}")
    public ResponseEntity<MeetingTypeResponse> getMeetingTypeByIdUnauthenticated(
            @PathVariable String userLink,
            @PathVariable Long meetingTypeId) {

        MeetingTypeResponse meetingTypeResponse = meetingTypeService.getMeetingTypeByUserLinkAndId(userLink, meetingTypeId);
        return ResponseEntity.ok(meetingTypeResponse);
    }

    @GetMapping
    public ResponseEntity<List<MeetingTypeResponse>> getAllMyMeetingTypes() {
        return ResponseEntity.ok(meetingTypeService.getAllMeetingTypesResponsesForCurrentUser());
    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlotsForMeetingType(@PathVariable Long id) {
        List<AvailableSlotResponse> availableSlots = availableSlotService.getAvailableSlotsResponse(id);
        return ResponseEntity.ok(availableSlots);
    }

    @GetMapping("/{userLink}/{id}/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlotsForMeetingTypeUnauthenticated(@PathVariable String userLink, @PathVariable Long id) {
        List<AvailableSlotResponse> availableSlots = availableSlotService.getAvailableSlotsResponseUnauthenticated(userLink, id);
        return ResponseEntity.ok(availableSlots);
    }

    @PostMapping("/{userLink}/{meetingTypeId}/slots/{slotId}/book")
    public ResponseEntity<Map<String, String>> bookSlot(
            @PathVariable String userLink,
            @PathVariable Long meetingTypeId,
            @PathVariable Long slotId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam Long locationId) {
        availableSlotService.bookAvailableSlot(userLink, meetingTypeId, slotId, name, email, locationId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Slot successfully booked");

        return ResponseEntity.ok(response);
    }
}