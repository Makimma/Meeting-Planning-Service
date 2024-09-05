package com.example.demo.service.impl;

import com.example.demo.entity.Location;
import com.example.demo.entity.MeetingType;
import com.example.demo.entity.MeetingTypeTimeRange;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidTimeRangeException;
import com.example.demo.exception.MeetingTypeNotFoundException;
import com.example.demo.repository.MeetingTypeRepository;
import com.example.demo.request.MeetingTypeRequest;
import com.example.demo.request.MeetingTypeTimeRangeRequest;
import com.example.demo.response.LocationResponse;
import com.example.demo.response.MeetingTypeResponse;
import com.example.demo.response.MeetingTypeTimeRangeResponse;
import com.example.demo.service.AvailableSlotService;
import com.example.demo.service.LocationService;
import com.example.demo.service.MeetingTypeService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeetingTypeServiceImpl implements MeetingTypeService {
    private final MeetingTypeRepository meetingTypeRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final AvailableSlotService availableSlotService;

    public MeetingTypeServiceImpl(MeetingTypeRepository meetingTypeRepository,
                                  UserService userService,
                                  LocationService locationService,
                                  AvailableSlotService availableSlotService) {
        this.meetingTypeRepository = meetingTypeRepository;
        this.userService = userService;
        this.locationService = locationService;
        this.availableSlotService = availableSlotService;
    }

    @Override
    @Transactional
    public MeetingTypeResponse createMeetingType(MeetingTypeRequest request) {
        User currentUser = userService.findByEmail(AuthUtils.getCurrentUserEmail());MeetingType meetingType = new MeetingType();

        validateTimeRanges(request.getTimeRanges());

        meetingType.setTitle(request.getTitle());
        meetingType.setDescription(request.getDescription());
        meetingType.setDurationMinutes(request.getDuration());
        meetingType.setMaxDaysInAdvance(request.getMaxDaysInAdvance());
        meetingType.setUser(currentUser);

        List<Location> locations = locationService.findAllByLocationId(request.getLocationIds());
        meetingType.setLocations(locations);
        meetingType = meetingTypeRepository.save(meetingType);

        MeetingType finalMeetingType = meetingType;
        List<MeetingTypeTimeRange> timeRanges = request.getTimeRanges().stream()
                .map(rangeRequest -> {
                    MeetingTypeTimeRange range = new MeetingTypeTimeRange();
                    range.setDayOfWeek(rangeRequest.getDayOfWeek());
                    range.setStartTime(rangeRequest.getStartTime());
                    range.setEndTime(rangeRequest.getEndTime());
                    range.setMeetingType(finalMeetingType);
                    return range;
                }).collect(Collectors.toList());
        meetingType.setTimeRanges(timeRanges);

        availableSlotService.createAvailableSlots(meetingType);

        return toMeetingTypeResponse(meetingTypeRepository.save(meetingType));
    }

    @Override
    public MeetingTypeResponse getMeetingTypeResponseById(Long meetingTypeId) {
        return toMeetingTypeResponse(getMeetingTypeById(meetingTypeId));
    }

    @Override
    public MeetingType getMeetingTypeById(Long meetingTypeId) {
        MeetingType meetingType = meetingTypeRepository.findById(meetingTypeId)
                .orElseThrow(() -> new MeetingTypeNotFoundException("Meeting Type Not Found"));
        if (!meetingType.getUser().getEmail().equals(AuthUtils.getCurrentUserEmail())) {
            throw new MeetingTypeNotFoundException("Meeting Type Not Found");
        }

        return meetingType;
    }

    @Override
    public List<MeetingType> getAllMeetingTypes() {
        return meetingTypeRepository.findAllByUser(userService.findByEmail(AuthUtils.getCurrentUserEmail()));
    }

    @Override
    public List<MeetingTypeResponse> getAllMeetingTypesResponsesForCurrentUser() {
        return getAllMeetingTypes()
                .stream()
                .map(this::toMeetingTypeResponse)
                .toList();
    }

    private MeetingTypeResponse toMeetingTypeResponse(MeetingType meetingType) {
        MeetingTypeResponse response = new MeetingTypeResponse();
        response.setId(meetingType.getId());
        response.setTitle(meetingType.getTitle());
        response.setDescription(meetingType.getDescription());
        response.setDuration(meetingType.getDurationMinutes());
        response.setMaxDaysInAdvance(meetingType.getMaxDaysInAdvance());

        List<LocationResponse> locationResponses = meetingType.getLocations().stream()
                .map(location -> {
                    LocationResponse locationResponse = new LocationResponse();
                    locationResponse.setId(location.getId());
                    locationResponse.setName(location.getName());
                    return locationResponse;
                }).collect(Collectors.toList());
        response.setLocations(locationResponses);

        List<MeetingTypeTimeRangeResponse> timeRangeResponses = meetingType.getTimeRanges().stream()
                .map(timeRange -> {
                    MeetingTypeTimeRangeResponse rangeResponse = new MeetingTypeTimeRangeResponse();
                    rangeResponse.setDayOfWeek(timeRange.getDayOfWeek());
                    rangeResponse.setStartTime(timeRange.getStartTime());
                    rangeResponse.setEndTime(timeRange.getEndTime());
                    return rangeResponse;
                }).collect(Collectors.toList());
        response.setTimeRanges(timeRangeResponses);

        return response;
    }

    private void validateTimeRanges(List<MeetingTypeTimeRangeRequest> timeRanges) {
        Map<DayOfWeek, List<MeetingTypeTimeRangeRequest>> groupedRanges = timeRanges.stream()
                .collect(Collectors.groupingBy(MeetingTypeTimeRangeRequest::getDayOfWeek));

        for (Map.Entry<DayOfWeek, List<MeetingTypeTimeRangeRequest>> entry : groupedRanges.entrySet()) {
            List<MeetingTypeTimeRangeRequest> rangesForDay = entry.getValue();

            rangesForDay.sort(Comparator.comparing(MeetingTypeTimeRangeRequest::getStartTime));

            for (int i = 0; i < rangesForDay.size() - 1; ++i) {
                MeetingTypeTimeRangeRequest current = rangesForDay.get(i);
                MeetingTypeTimeRangeRequest next = rangesForDay.get(i + 1);

                if (current.getEndTime().isAfter(next.getStartTime())) {
                    throw new InvalidTimeRangeException("Invalid time range");
                }
            }
        }
    }
}