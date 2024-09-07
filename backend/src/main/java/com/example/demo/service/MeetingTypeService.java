package com.example.demo.service;

import com.example.demo.entity.MeetingType;
import com.example.demo.request.MeetingTypeRequest;
import com.example.demo.response.MeetingTypeResponse;

import java.util.List;

public interface MeetingTypeService {
    MeetingTypeResponse createMeetingType(MeetingTypeRequest meetingTypeRequest);

    List<MeetingType> getAllMeetingTypes();

    List<MeetingTypeResponse> getAllMeetingTypesResponsesForCurrentUser();

    MeetingTypeResponse getMeetingTypeResponseById(Long meetingTypeId);

    MeetingType getMeetingTypeById(Long meetingTypeId);

    MeetingType getMeetingTypeByIdUnauthenticated(Long meetingTypeId);
}
