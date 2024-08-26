package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.exception.*;
import com.example.demo.repository.*;
import com.example.demo.response.*;
import com.example.demo.request.TimeSlotRequest;
import com.example.demo.request.VoteRequest;
import com.example.demo.service.MeetingPollService;
import com.example.demo.service.UserService;
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
    private final MeetingPollRepository meetingPollRepository;
    private final UserService userService;
    private final LocationRepository locationRepository;
    private final MeetingPollTimeSlotRepository meetingPollTimeSlotRepository;
    private final MeetingPollParticipantRepository meetingPollParticipantRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    public MeetingPollServiceImpl(MeetingPollRepository meetingPollRepository,
                                  UserService userService,
                                  LocationRepository locationRepository,
                                  MeetingPollTimeSlotRepository meetingPollTimeSlotRepository,
                                  MeetingPollParticipantRepository meetingPollParticipantRepository,
                                  MeetingRepository meetingRepository,
                                  MeetingParticipantRepository meetingParticipantRepository) {
        this.meetingPollRepository = meetingPollRepository;
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.meetingPollTimeSlotRepository = meetingPollTimeSlotRepository;
        this.meetingPollParticipantRepository = meetingPollParticipantRepository;
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
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

        meetingPoll.setUser(userService.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElseThrow(() -> new UserNotFoundException(AuthUtils.getCurrentUserEmail())));

        meetingPoll.setLocation(locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Invalid Location")));

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
        MeetingPoll meetingPoll = meetingPollRepository.findById(meetingPollId)
                .orElseThrow(() -> new RuntimeException("MeetingPoll not found"));

        if (!meetingPoll.getUser().getId().equals(
                userService.findByEmail(AuthUtils.getCurrentUserEmail())
                        .orElseThrow(() -> new UserNotFoundException("User Not Found")).getId())) {
            throw new UserNotFoundException("User Not Found");
        }

        return toMeetingPollResponse(meetingPoll);
    }

    @Override
    @Transactional
    public void deleteMeetingPoll(Long meetingPollId) {
        if (!meetingPollRepository.findById(meetingPollId)
                .orElseThrow(() -> new MeetingPollNotFoundException("Meeting Poll not found")).getUser().getId().equals(
                        userService.findByEmail(AuthUtils.getCurrentUserEmail())
                                .orElseThrow(() -> new UserNotFoundException("User Not Found")).getId())) {
            throw new UserNotFoundException("User Not Found");
        }
        meetingPollRepository.deleteById(meetingPollId);
    }

    @Override
    public List<MeetingPollResponse> getMeetingPollsByUser() {
        List<MeetingPoll> meetingPolls = meetingPollRepository.findAllByUser(userService.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found")));

        return meetingPolls.stream()
                .map(this::toMeetingPollResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingPollResponse getMeetingPollByUserLinkAndId(String userLink, Long meetingPollId) {
        userService.findByLink(userLink)
                .orElseThrow(() -> new UserNotFoundException("Meeting poll not found"));

        MeetingPoll meetingPoll = meetingPollRepository.findById(meetingPollId)
                .orElseThrow(() -> new MeetingPollNotFoundException("MeetingPoll not found"));

        return toMeetingPollResponse(meetingPoll);
    }

    @Override
    @Transactional
    public void vote(String userLink, Long meetingPollId, VoteRequest voteRequest) {
        userService.findByLink(userLink)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        MeetingPoll meetingPoll = meetingPollRepository.findById(meetingPollId)
                .orElseThrow(() -> new MeetingPollNotFoundException("MeetingPoll not found"));

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
        MeetingPoll meetingPoll = meetingPollRepository.findById(meetingPollId)
                .orElseThrow(() -> new RuntimeException("MeetingPoll not found"));

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
        MeetingPoll meetingPoll = meetingPollRepository.findById(meetingPollId)
                .orElseThrow(() -> new MeetingPollNotFoundException("MeetingPoll not found"));

        if (!meetingPoll.getUser().getEmail().equals(AuthUtils.getCurrentUserEmail())) {
            throw new UserNotFoundException("User not found");
        }

        if (!meetingPoll.isActive()) {
            throw new MeetingAlreadyExistException("Meeting already created");
        }

        MeetingPollTimeSlot meetingPollTimeSlot = meetingPollTimeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new TimeSlotNotFoundException("TimeSlot not found"));
        if (!meetingPollTimeSlot.getMeetingPoll().getId().equals(meetingPoll.getId())) {
            throw new TimeSlotNotFoundException("TimeSlot not found");
        }

        List<MeetingParticipant> meetingParticipants = meetingPoll.getMeetingPollParticipants().stream()
                .map(participant -> {
                    MeetingParticipant newParticipant = new MeetingParticipant();
                    newParticipant.setParticipantName(participant.getParticipantName());
                    newParticipant.setParticipantEmail(participant.getParticipantEmail());
                    return newParticipant;
                }).toList();

        Meeting meeting = Meeting.builder()
                .title(meetingPoll.getTitle())
                .beginAt(meetingPollTimeSlot.getBeginAt())
                .endAt(meetingPollTimeSlot.getEndAt())
                .description(meetingPoll.getDescription())
                .participants(meetingParticipants)
                .build();

        meeting = meetingRepository.save(meeting);
        Meeting finalMeeting = meeting;
        meetingParticipants.forEach(participant -> {participant.setMeeting(finalMeeting);});
        meetingParticipantRepository.saveAll(meetingParticipants);

        //TODO отправить участникам письмо о ивенте

        //TODO добавить пользователю в календарь ивент

        //TODO добавить логику в зависимости от location(зум, гугл мит и тд)

        meetingPoll.setActive(false);
        meetingPollRepository.save(meetingPoll);

        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .description(meeting.getDescription())
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
