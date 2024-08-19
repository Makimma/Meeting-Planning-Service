package com.example.demo.service.impl;

import com.example.demo.dto.CreateMeetingFromPollDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.*;
import com.example.demo.repository.MeetingPollParticipantRepository;
import com.example.demo.repository.MeetingPollTimeSlotRepository;
import com.example.demo.response.ParticipantResponse;
import com.example.demo.response.VoteCountResponse;
import com.example.demo.request.TimeSlotRequest;
import com.example.demo.request.VoteRequest;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.MeetingPollRepository;
import com.example.demo.response.MeetingPollResponse;
import com.example.demo.response.TimeSlotResponse;
import com.example.demo.service.MeetingPollService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public MeetingPollServiceImpl(MeetingPollRepository meetingPollRepository,
                                  UserService userService,
                                  LocationRepository locationRepository,
                                  MeetingPollTimeSlotRepository meetingPollTimeSlotRepository,
                                  MeetingPollParticipantRepository meetingPollParticipantRepository) {
        this.meetingPollRepository = meetingPollRepository;
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.meetingPollTimeSlotRepository = meetingPollTimeSlotRepository;
        this.meetingPollParticipantRepository = meetingPollParticipantRepository;
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
                    !Duration.between(timeSlotRequest.getBeginAt(), timeSlotRequest.getEndAt()).equals(Duration.ofMinutes(duration))) {
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

        List<MeetingPollTimeSlot> selectedSlots = meetingPollTimeSlotRepository.findAllById(voteRequest.getSelectedTimeSlotIds());
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
    public ResponseEntity<?> createMeetingFromPoll(CreateMeetingFromPollDTO createMeetingFromPollDTO) {
        //TODO
//        if (scheduledPollEventService.schedulePollEvent(createMeetingFromPollDTO)) {
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        }
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return null;
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
