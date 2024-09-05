package com.example.demo.service;

import com.example.demo.entity.AvailableSlot;
import com.example.demo.entity.MeetingType;
import com.example.demo.response.AvailableSlotResponse;

import java.time.ZonedDateTime;
import java.util.List;

public interface AvailableSlotService {
    void updateAllAvailableSlots();

    void createNewSlotsForMeetingType(MeetingType meetingType);

    void createAvailableSlots(MeetingType meetingType);

    List<AvailableSlotResponse> getAvailableSlotsResponse(Long meetingTypeId);

    void bookAvailableSlot(String userLink, Long slotId, String name, String email);
}
