package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.exception.AvailableSlotNotFoundException;
import com.example.demo.exception.LocationNotFoundException;
import com.example.demo.exception.MeetingTypeNotFoundException;
import com.example.demo.repository.AvailableSlotRepository;
import com.example.demo.repository.MeetingTypeLocationRepository;
import com.example.demo.repository.MeetingTypeTimeRangeRepository;
import com.example.demo.response.AvailableSlotResponse;
import com.example.demo.service.AvailableSlotService;
import com.example.demo.service.LocationService;
import com.example.demo.service.MeetingTypeService;
import com.example.demo.util.AuthUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AvailableSlotServiceImpl implements AvailableSlotService {
    private final AvailableSlotRepository availableSlotRepository;
    private final MeetingTypeTimeRangeRepository meetingTypeTimeRangeRepository;
    private final MeetingTypeService meetingTypeService;
    private final LocationService locationService;
    private final MeetingTypeLocationRepository meetingTypeLocationRepository;

    @Autowired
    public AvailableSlotServiceImpl(AvailableSlotRepository availableSlotRepository,
                                    MeetingTypeTimeRangeRepository meetingTypeTimeRangeRepository,
                                    @Lazy MeetingTypeService meetingTypeService,
                                    LocationService locationService, MeetingTypeLocationRepository meetingTypeLocationRepository) {
        this.availableSlotRepository = availableSlotRepository;
        this.meetingTypeTimeRangeRepository = meetingTypeTimeRangeRepository;
        this.meetingTypeService = meetingTypeService;
        this.locationService = locationService;
        this.meetingTypeLocationRepository = meetingTypeLocationRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void updateAllAvailableSlots() {
        deleteExpiredSlots();

        //TODO если встречу вырубить, то слоты не делать
        List<MeetingType> meetingTypes = meetingTypeService.getAllMeetingTypes();
        for (MeetingType meetingType : meetingTypes) {
            createNewSlotsForMeetingType(meetingType);
        }
    }

    @Override
    @Transactional
    public void createAvailableSlots(MeetingType meetingType) {
        createNewSlotsForMeetingType(meetingType);
    }

    @Transactional
    public void deleteExpiredSlots() {
        ZonedDateTime now = ZonedDateTime.now();
        availableSlotRepository.deleteByStartDateTimeBefore(now);
    }

    @Override
    @Transactional
    public void createNewSlotsForMeetingType(MeetingType meetingType) {
        int daysInAdvance = meetingType.getMaxDaysInAdvance();
        int meetingDurationMinutes = meetingType.getDurationMinutes();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime endDate = now.plusDays(daysInAdvance);

        while (now.isBefore(endDate)) {
            DayOfWeek dayOfWeek = now.getDayOfWeek();
            List<MeetingTypeTimeRange> timeRanges = meetingTypeTimeRangeRepository.findByMeetingTypeAndDayOfWeek(meetingType, dayOfWeek);

            for (MeetingTypeTimeRange timeRange : timeRanges) {
                ZonedDateTime slotStart = now.with(timeRange.getStartTime());
                ZonedDateTime slotEnd = now.with(timeRange.getEndTime());

                while (slotStart.isBefore(slotEnd)) {
                    ZonedDateTime nextSlotEnd = slotStart.plusMinutes(meetingDurationMinutes);
                    if (nextSlotEnd.isAfter(slotEnd)) {
                        break;
                    }
                    createSlotIfNotExists(meetingType, slotStart, nextSlotEnd);
                    slotStart = nextSlotEnd;
                }
            }

            now = now.plusDays(1);
        }
    }

    //TODO если встречу вырубить, то слоты нельзя бронировать
    @Override
    @Transactional
    public void bookAvailableSlot(String userLink,
                                  Long meetingTypeId,
                                  Long slotId,
                                  String name,
                                  String email,
                                  Long locationId) {
        MeetingType meetingType = meetingTypeService.getMeetingTypeById(meetingTypeId);

        if (!meetingType.getUser().getLink().equals(userLink)) {
            throw new IllegalArgumentException("Переданный линк не принадлежит владельцу типа встречи.");
        }

        AvailableSlot slot = availableSlotRepository.findById(slotId)
                .orElseThrow(() -> new AvailableSlotNotFoundException("Slot not found"));

        //TODO
        if (slot.isReserved()) {
            throw new IllegalArgumentException("Слот уже зарезервирован.");
        }

        MeetingTypeLocation location = meetingTypeLocationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));

        if (!meetingType.getLocations().contains(location)) {
            throw new LocationNotFoundException("Location not found");
        }

        slot.setName(name);
        slot.setEmail(email);
        slot.setReserved(true);
        availableSlotRepository.save(slot);

        //TODO createMeeting(slot, name, email, location);
    }

    @Transactional
    public void createSlotIfNotExists(MeetingType meetingType, ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
        boolean slotExists = availableSlotRepository.existsByMeetingTypeAndStartDateTime(meetingType, startDateTime);
        if (!slotExists) {
            AvailableSlot slot = new AvailableSlot();
            slot.setMeetingType(meetingType);
            slot.setStartDateTime(startDateTime);
            slot.setEndDateTime(endDateTime);
            slot.setReserved(false);
            availableSlotRepository.save(slot);
        }
    }

    @Override
    public List<AvailableSlotResponse> getAvailableSlotsResponse(Long meetingTypeId) {
        return getAvailableSlots(meetingTypeId).stream().map(this::toAvailableSlotResponse).toList();
    }

    public List<AvailableSlot> getAvailableSlots(Long meetingTypeId) {
        MeetingType meetingType = meetingTypeService.getMeetingTypeById(meetingTypeId);
        if (!meetingType.getUser().getEmail().equals(AuthUtils.getCurrentUserEmail())) {
            throw new MeetingTypeNotFoundException("Meeting Type not found");
        }
        return availableSlotRepository.findByMeetingTypeIdAndStartDateTimeAfterAndReservedFalse(meetingTypeId, ZonedDateTime.now());
    }

    private AvailableSlotResponse toAvailableSlotResponse(AvailableSlot slot) {
        AvailableSlotResponse response = new AvailableSlotResponse();
        response.setId(slot.getId());
        response.setStartTime(slot.getStartDateTime());
        response.setEndTime(slot.getEndDateTime());
        return response;
    }
}
