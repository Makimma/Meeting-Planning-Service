package com.example.demo.service;

import com.example.demo.entity.MeetingType;
import com.example.demo.response.AvailableSlotResponse;

import java.util.List;

public interface AvailableSlotService {
    void updateAllAvailableSlots();

    void createNewSlotsForMeetingType(MeetingType meetingType);

    void createAvailableSlots(MeetingType meetingType);

    List<AvailableSlotResponse> getAvailableSlotsResponse(Long meetingTypeId);

    void bookAvailableSlot(String userLink, Long meetingTypeId, Long availableSlotId, String name, String email, Long locationId);

    List<AvailableSlotResponse> getAvailableSlotsResponseUnauthenticated(String userLink, Long id);
}
