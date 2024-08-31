package com.example.demo.service.impl;

import com.example.demo.entity.Meeting;
import com.example.demo.exception.MeetingNotFoundException;
import com.example.demo.repository.MeetingRepository;
import com.example.demo.service.GoogleCalendarService;
import com.example.demo.service.MeetingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final GoogleCalendarService googleCalendarService;

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository, GoogleCalendarService googleCalendarService) {
        this.meetingRepository = meetingRepository;
        this.googleCalendarService = googleCalendarService;
    }

    @Override
    @Transactional
    public void deleteMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found"));

        if (meeting.getEventId() != null) {
            if (meeting.getCalendar().getName().equals("Google")) {
                googleCalendarService.deleteEventFromCalendar(meeting.getCalendar(), meeting.getEventId());
            }
        }

        meetingRepository.delete(meeting);
    }
}
