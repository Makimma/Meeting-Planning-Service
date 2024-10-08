package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.exception.InvalidTimeRangeException;
import com.example.demo.exception.MeetingTypeNotFoundException;
import com.example.demo.repository.MeetingTypeLocationRepository;
import com.example.demo.repository.MeetingTypeRepository;
import com.example.demo.request.MeetingTypeRequest;
import com.example.demo.request.MeetingTypeTimeRangeRequest;
import com.example.demo.request.MeetingTypeUpdateRequest;
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
    private final MeetingTypeLocationRepository meetingTypeLocationRepository;

    public MeetingTypeServiceImpl(MeetingTypeRepository meetingTypeRepository,
                                  UserService userService,
                                  LocationService locationService,
                                  AvailableSlotService availableSlotService, MeetingTypeLocationRepository meetingTypeLocationRepository) {
        this.meetingTypeRepository = meetingTypeRepository;
        this.userService = userService;
        this.locationService = locationService;
        this.availableSlotService = availableSlotService;
        this.meetingTypeLocationRepository = meetingTypeLocationRepository;
    }

    @Override
    @Transactional
    public MeetingTypeResponse createMeetingType(MeetingTypeRequest request) {
        User currentUser = userService.findByEmail(AuthUtils.getCurrentUserEmail());

        validateTimeRanges(request.getTimeRanges());

        MeetingType meetingType = new MeetingType();
        meetingType.setTitle(request.getTitle());
        meetingType.setDescription(request.getDescription());
        meetingType.setDurationMinutes(request.getDuration());
        meetingType.setMaxDaysInAdvance(request.getMaxDaysInAdvance());
        meetingType.setUser(currentUser);

        List<MeetingTypeLocation> locations = request.getLocations().stream().map(locationRequest -> {
            MeetingTypeLocation meetingTypeLocation = new MeetingTypeLocation();
            meetingTypeLocation.setLocation(locationService.findById(locationRequest.getId()));
            meetingTypeLocation.setAddress(locationService.findById(locationRequest.getId()).getName().equals("In-Person") ? locationRequest.getAddress() : "");
            return meetingTypeLocationRepository.save(meetingTypeLocation);
        }).toList();

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

        return toMeetingTypeResponse(meetingType);
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
    public MeetingType getMeetingTypeByIdUnauthenticated(Long meetingTypeId) {
        return meetingTypeRepository.findById(meetingTypeId)
                .orElseThrow(() -> new MeetingTypeNotFoundException("Meeting Type Not Found"));
    }

    @Override
    public MeetingTypeResponse getMeetingTypeByUserLinkAndId(String userLink, Long meetingTypeId) {
        MeetingType meetingType = meetingTypeRepository.findById(meetingTypeId)
                .orElseThrow(() -> new MeetingTypeNotFoundException("Meeting Type not found"));

        if (!meetingType.getUser().getLink().equals(userLink)) {
            throw new MeetingTypeNotFoundException("Meeting Type not found");
        }

        return toMeetingTypeResponse(meetingType);
    }

    @Override
    @Transactional
    public MeetingTypeResponse patchMeetingType(Long meetingTypeId, MeetingTypeUpdateRequest updateRequest) {
        MeetingType meetingType = getMeetingTypeById(meetingTypeId);

        if (!meetingType.getUser().getEmail().equals(AuthUtils.getCurrentUserEmail())) {
            throw new MeetingTypeNotFoundException("Meeting Type Not Found");
        }

        if (updateRequest.getTitle() != null) {
            meetingType.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getDescription() != null) {
            meetingType.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getDurationMinutes() != null) {
            meetingType.setDurationMinutes(updateRequest.getDurationMinutes());
        }

        if (updateRequest.getMaxDaysInAdvance() != null) {
            meetingType.setMaxDaysInAdvance(updateRequest.getMaxDaysInAdvance());
        }

//        if (updateRequest.getLocations() != null) {
//            updateLocations(meetingType, updateRequest.getLocations());
//        }
//
//        if (updateRequest.getTimeRanges() != null) {
//            updateTimeRanges(meetingType, updateRequest.getTimeRanges());
//        }

        return toMeetingTypeResponse(meetingTypeRepository.save(meetingType));
    }

//    private void updateLocations(MeetingType meetingType, List<LocationRequest> locationRequests) {
//        List<MeetingTypeLocation> updatedLocations = meetingType.getLocations().stream()
//                .filter(location -> locationRequests.stream()
//                        .noneMatch(request -> Boolean.TRUE.equals(request.getDelete()) && request.getId().equals(location.getId())))
//                .collect(Collectors.toList());
//
//        for (LocationRequest locationRequest : locationRequests) {
//            if (Boolean.TRUE.equals(locationRequest.getDelete())) {
//                continue;
//            }
//
//            if (locationRequest.getId() == null) {
//                // Если ID нет, добавляем новую локацию
//                MeetingTypeLocation newLocation = new MeetingTypeLocation();
//                newLocation.setAddress(locationRequest.getAddress());
//                newLocation.setLocation(locationRequest.getLocation());
//                updatedLocations.add(newLocation);
//            } else {
//                // Обновляем существующую локацию
//                MeetingTypeLocation existingLocation = meetingType.getLocations().stream()
//                        .filter(location -> location.getId().equals(locationRequest.getId()))
//                        .findFirst()
//                        .orElseThrow(() -> new IllegalArgumentException("Локация с ID " + locationRequest.getId() + " не найдена."));
//                existingLocation.setAddress(locationRequest.getAddress());
//            }
//        }
//
//        meetingType.getLocations().clear();
//        meetingType.getLocations().addAll(updatedLocations);
//    }
//
//    private void updateTimeRanges(MeetingType meetingType, List<MeetingTypeTimeRangeRequest> timeRangeRequests) {
//        meetingType.getTimeRanges().clear();
//
//        // Добавляем новые временные диапазоны
//        List<MeetingTypeTimeRange> updatedTimeRanges = timeRangeRequests.stream()
//                .map(timeRangeRequest -> {
//                    MeetingTypeTimeRange timeRange = new MeetingTypeTimeRange();
//                    timeRange.setDayOfWeek(timeRangeRequest.getDayOfWeek());
//                    timeRange.setStartTime(timeRangeRequest.getStartTime());
//                    timeRange.setEndTime(timeRangeRequest.getEndTime());
//                    return timeRange;
//                })
//                .toList();
//
//        meetingType.getTimeRanges().addAll(updatedTimeRanges);
//    }

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
        response.setLocations(meetingType.getLocations());
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