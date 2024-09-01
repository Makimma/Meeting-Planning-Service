package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.exception.*;
import com.example.demo.repository.*;
import com.example.demo.response.*;
import com.example.demo.request.TimeSlotRequest;
import com.example.demo.request.VoteRequest;
import com.example.demo.service.*;
import com.example.demo.util.AuthUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
public class MeetingPollServiceImpl implements MeetingPollService {
    private final UserService userService;
    private final MeetingService meetingService;
    private final CalendarService calendarService;
    private final LocationService locationService;
    private final GoogleCalendarService googleCalendarService;
    private final MeetingPollRepository meetingPollRepository;
    private final ConnectedCalendarRepository connectedCalendarRepository;
    private final MeetingPollTimeSlotRepository meetingPollTimeSlotRepository;
    private final MeetingPollParticipantRepository meetingPollParticipantRepository;

    @Autowired
    public MeetingPollServiceImpl(MeetingPollRepository meetingPollRepository,
                                  UserService userService,
                                  MeetingPollTimeSlotRepository meetingPollTimeSlotRepository,
                                  MeetingPollParticipantRepository meetingPollParticipantRepository,
                                  GoogleCalendarService googleCalendarService,
                                  ConnectedCalendarRepository connectedCalendarRepository,
                                  CalendarService calendarService,
                                  MeetingServiceImpl meetingService,
                                  LocationService locationService) {
        this.userService = userService;
        this.meetingService = meetingService;
        this.calendarService = calendarService;
        this.locationService = locationService;
        this.googleCalendarService = googleCalendarService;
        this.meetingPollRepository = meetingPollRepository;
        this.connectedCalendarRepository = connectedCalendarRepository;
        this.meetingPollTimeSlotRepository = meetingPollTimeSlotRepository;
        this.meetingPollParticipantRepository = meetingPollParticipantRepository;
    }

    @Override
    public MeetingPoll findById(Long id) {
        return meetingPollRepository.findById(id)
                .orElseThrow(() -> new MeetingPollNotFoundException("MeetingPoll not found"));
    }

    @Override
    @Transactional
    public MeetingPollResponse createMeetingPoll(String title,
                                                 String description,
                                                 int duration,
                                                 Long locationId,
                                                 List<TimeSlotRequest> timeSlotRequests) {
        MeetingPoll meetingPoll = new MeetingPoll();
        meetingPoll.setTitle(title);
        meetingPoll.setDescription(description);
        meetingPoll.setCreatedAt(ZonedDateTime.now());
        meetingPoll.setDuration(duration);
        meetingPoll.setUser(userService.findByEmail(AuthUtils.getCurrentUserEmail()));
        meetingPoll.setLocation(locationService.findById(locationId));

        MeetingPoll savedPoll = meetingPollRepository.save(meetingPoll);

        List<MeetingPollTimeSlot> timeSlots = timeSlotRequests.stream().map(timeSlotRequest -> {
            if (timeSlotRequest.getBeginAt().isAfter(timeSlotRequest.getEndAt()) ||
                    !Duration.between(timeSlotRequest.getBeginAt(), timeSlotRequest.getEndAt()).equals(Duration.ofMinutes(duration)) ||
                    timeSlotRequest.getBeginAt().isBefore(ZonedDateTime.now())) {
                throw new InvalidTimeException("Invalid time");
            }
            MeetingPollTimeSlot timeSlot = new MeetingPollTimeSlot();
            timeSlot.setBeginAt(timeSlotRequest.getBeginAt());
            timeSlot.setEndAt(timeSlotRequest.getEndAt());
            timeSlot.setMeetingPoll(savedPoll);
            return meetingPollTimeSlotRepository.save(timeSlot);
        }).collect(Collectors.toList());

        savedPoll.setMeetingPollTimeSlots(timeSlots);

        return toMeetingPollResponse(meetingPoll);
    }

    @Override
    public MeetingPollResponse getMeetingPollInfo(Long meetingPollId) {
        MeetingPoll meetingPoll = findById(meetingPollId);
        if (!meetingPoll.getUser().getId().equals(
                userService.findByEmail(AuthUtils.getCurrentUserEmail()).getId())) {
            throw new MeetingPollNotFoundException("MeetingPoll not found");
        }

        return toMeetingPollResponse(meetingPoll);
    }

    @Override
    @Transactional
    public void deleteMeetingPoll(Long meetingPollId) {
        if (!findById(meetingPollId).getUser().getId().equals(
                        userService.findByEmail(AuthUtils.getCurrentUserEmail()).getId())) {
            throw new MeetingPollNotFoundException("MeetingPoll not found");
        }
        meetingPollRepository.deleteById(meetingPollId);
    }

    @Override
    public List<MeetingPollResponse> getMeetingPollsByUser() {
        List<MeetingPoll> meetingPolls = meetingPollRepository
                .findAllByUser(userService.findByEmail(AuthUtils.getCurrentUserEmail()));

        return meetingPolls.stream()
                .map(this::toMeetingPollResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingPollResponse getMeetingPollByUserLinkAndId(String userLink, Long meetingPollId) {
        userService.findByLink(userLink);

        MeetingPoll meetingPoll = findById(meetingPollId);

        return toMeetingPollResponse(meetingPoll);
    }

    @Override
    @Transactional
    public void vote(String userLink, Long meetingPollId, VoteRequest voteRequest) {
        userService.findByLink(userLink);

        MeetingPoll meetingPoll = findById(meetingPollId);
        if (meetingPollParticipantRepository.existsByMeetingPollAndParticipantEmail(meetingPoll, voteRequest.getParticipantEmail())) {
            throw new UserAlreadyVoteException("User has already voted in this poll");
        }

        List<MeetingPollTimeSlot> selectedSlots = meetingPollTimeSlotRepository
                .findByIdInAndMeetingPollId(voteRequest.getSelectedTimeSlotIds(), meetingPollId);
        if (selectedSlots.size() != voteRequest.getSelectedTimeSlotIds().size()) {
            throw new TimeSlotNotFoundException("Time slot not found");
        }

        MeetingPollParticipant participant = new MeetingPollParticipant();
        participant.setMeetingPoll(meetingPoll);
        participant.setParticipantName(voteRequest.getParticipantName());
        participant.setParticipantEmail(voteRequest.getParticipantEmail());
        participant.setSelectedTimeSlots(selectedSlots);
        meetingPollParticipantRepository.save(participant);

        //TODO на почту отправить что проголосовал
    }

    @Override
    public List<VoteCountResponse> getVoteCountsForMeetingPoll(Long meetingPollId) {
        MeetingPoll meetingPoll = findById(meetingPollId);
        if (!meetingPoll.getUser().getEmail().equals(AuthUtils.getCurrentUserEmail())) {
            throw new UserNotFoundException("User Not Found");
        }

        List<MeetingPollTimeSlot> timeSlots = meetingPoll.getMeetingPollTimeSlots();

        return timeSlots.stream().map(timeSlot -> {
            List<MeetingPollParticipant> participants = meetingPollParticipantRepository.findAllBySelectedTimeSlotsContaining(timeSlot);

            List<ParticipantResponse> participantResponses = participants.stream().map(participant -> {
                ParticipantResponse response = new ParticipantResponse();
                response.setParticipantName(participant.getParticipantName());
                response.setParticipantEmail(participant.getParticipantEmail());
                return response;
            }).collect(Collectors.toList());

            VoteCountResponse voteCountResponse = new VoteCountResponse();
            voteCountResponse.setTimeSlotId(timeSlot.getId());
            voteCountResponse.setBeginAt(timeSlot.getBeginAt());
            voteCountResponse.setEndAt(timeSlot.getEndAt());
            voteCountResponse.setVoteCount(participants.size());
            voteCountResponse.setParticipants(participantResponses);
            return voteCountResponse;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MeetingResponse createMeetingFromPoll(Long meetingPollId, Long timeSlotId) {
        MeetingPoll meetingPoll = findById(meetingPollId);
        if (!meetingPoll.getUser().getEmail().equals(AuthUtils.getCurrentUserEmail())) {
            throw new UserNotFoundException("User not found");
        } else if (!meetingPoll.isActive()) {
            throw new MeetingAlreadyExistException("Meeting already created");
        }

        MeetingPollTimeSlot meetingPollTimeSlot = meetingPollTimeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new TimeSlotNotFoundException("TimeSlot not found"));
        if (!meetingPollTimeSlot.getMeetingPoll().getId().equals(meetingPoll.getId())) {
            throw new TimeSlotNotFoundException("TimeSlot not found");
        }

        Meeting meeting = meetingService.createMeeting(meetingPoll, meetingPollTimeSlot);

        //TODO добавить логику создания ссылки на встречу в зависимости от location(гугл мит и тд)

        //Добавление встречи в календарь пользователя
        Calendar calendar = calendarService.findByName("Google");
        if (connectedCalendarRepository.existsByUserAndCalendar(meeting.getUser(), calendar)) {
            meeting.setCalendar(calendar);
            String eventId = googleCalendarService.createEvent(meeting.getUser(), meeting);
            if (eventId != null) {
                meeting.setEventId(eventId);
                meeting.setCalendar(calendar);
            }
        }

        meetingPoll.setActive(false);
        meetingPollRepository.save(meetingPoll);

        return toMeetingResponse(meeting);
    }

    private MeetingResponse toMeetingResponse(Meeting meeting) {
        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .description(meeting.getDescription())
                .locationId(meeting.getLocation().getId())
                .beginAt(meeting.getBeginAt())
                .endAt(meeting.getEndAt())
                .participants(meeting.getParticipants().stream()
                        .map(participant -> new ParticipantResponse(participant.getParticipantName(), participant.getParticipantEmail()))
                        .toList())
                .build();
    }

    private MeetingPollResponse toMeetingPollResponse(MeetingPoll meetingPoll) {
        return MeetingPollResponse.builder()
                .id(meetingPoll.getId())
                .title(meetingPoll.getTitle())
                .duration(meetingPoll.getDuration())
                .description(meetingPoll.getDescription())
                .locationId(meetingPoll.getLocation().getId())
                .locationName(meetingPoll.getLocation().getName())
                .creatorName(meetingPoll.getUser().getUsername())
                .timeSlots(meetingPoll.getMeetingPollTimeSlots()
                        .stream()
                        .map(this::toTimeSlotResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private TimeSlotResponse toTimeSlotResponse(MeetingPollTimeSlot timeSlot) {
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .beginAt(timeSlot.getBeginAt())
                .endAt(timeSlot.getEndAt())
                .build();
    }
}
