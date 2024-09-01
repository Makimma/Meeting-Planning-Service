package com.example.demo.service.impl;

import com.example.demo.entity.Meeting;
import com.example.demo.entity.MeetingParticipant;
import com.example.demo.entity.MeetingPoll;
import com.example.demo.entity.MeetingPollTimeSlot;
import com.example.demo.exception.MeetingNotFoundException;
import com.example.demo.repository.MeetingParticipantRepository;
import com.example.demo.repository.MeetingRepository;
import com.example.demo.service.GoogleCalendarService;
import com.example.demo.service.MeetingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final GoogleCalendarService googleCalendarService;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository,
                              GoogleCalendarService googleCalendarService,
                              MeetingParticipantRepository meetingParticipantRepository) {
        this.meetingRepository = meetingRepository;
        this.googleCalendarService = googleCalendarService;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    @Override
    @Transactional
    public Meeting createMeeting(MeetingPoll meetingPoll, MeetingPollTimeSlot meetingPollTimeSlot) {
        Meeting meeting = Meeting.builder()
                .user(meetingPoll.getUser())
                .title(meetingPoll.getTitle())
                .description(meetingPoll.getDescription())
                .beginAt(meetingPollTimeSlot.getBeginAt())
                .endAt(meetingPollTimeSlot.getEndAt())
                .location(meetingPoll.getLocation())
                .build();
        meeting = meetingRepository.save(meeting);

        Meeting finalMeeting = meeting;
        List<MeetingParticipant> meetingParticipants = meetingPoll.getMeetingPollParticipants().stream()
                .map(participant -> {
                    MeetingParticipant newParticipant = new MeetingParticipant();
                    newParticipant.setParticipantName(participant.getParticipantName());
                    newParticipant.setParticipantEmail(participant.getParticipantEmail());
                    newParticipant.setMeeting(finalMeeting);
                    return meetingParticipantRepository.save(newParticipant);
                }).toList();

        meeting.setParticipants(meetingParticipants);
        return meeting;
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
