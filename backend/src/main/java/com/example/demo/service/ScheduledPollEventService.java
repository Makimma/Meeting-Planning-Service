package com.example.demo.service;

import com.example.demo.dto.CreateMeetingFromPollDTO;

import org.springframework.stereotype.Service;

public interface ScheduledPollEventService {
    void schedulePollEvent(CreateMeetingFromPollDTO createMeetingFromPollDTO);
//    void cancelPollEvent(CreateMeetingFromPollDTO createMeetingFromPollDTO);
//    void updatePollEvent(CreateMeetingFromPollDTO createMeetingFromPollDTO);
}
