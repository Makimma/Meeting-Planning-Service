package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.exception.MeetingNotFoundException;
import com.example.demo.repository.MeetingParticipantRepository;
import com.example.demo.repository.MeetingRepository;
import com.example.demo.response.MeetingResponse;
import com.example.demo.response.ParticipantResponse;
import com.example.demo.service.GoogleCalendarService;
import com.example.demo.service.MeetingService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final GoogleCalendarService googleCalendarService;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final UserService userService;

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository,
                              GoogleCalendarService googleCalendarService,
                              MeetingParticipantRepository meetingParticipantRepository,
                              UserService userService) {
        this.meetingRepository = meetingRepository;
        this.googleCalendarService = googleCalendarService;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.userService = userService;
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
                .physicalAddress(meetingPoll.getAddress())
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
                googleCalendarService.deleteEvent(meeting.getCalendar(), meeting.getEventId());
            }
        }
        meetingRepository.delete(meeting);
    }

    @Override
    public MeetingResponse getMeetingResponseById(Long id) {
        return toMeetingResponse(getMeetingById(id));
    }

    @Override
    public List<MeetingResponse> getAllUserMeetingResponses() {
        User user = userService.findByEmail(AuthUtils.getCurrentUserEmail());

        List<Meeting> meetings = meetingRepository.findAllByUser(user);
        return meetings.stream()
                .map(this::toMeetingResponse)
                .toList();
    }

    @Override
    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found"));
    }

    private MeetingResponse toMeetingResponse(Meeting meeting) {
        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .description(meeting.getDescription())
                .locationId(meeting.getLocation().getId())
                .address(meeting.getPhysicalAddress())
                .beginAt(meeting.getBeginAt())
                .endAt(meeting.getEndAt())
                .participants(meeting.getParticipants().stream()
                        .map(this::toParticipantResponse)
                        .toList())
                .build();
    }

    private ParticipantResponse toParticipantResponse(MeetingParticipant participant) {
        return new ParticipantResponse(participant.getParticipantName(), participant.getParticipantEmail());
    }
}
