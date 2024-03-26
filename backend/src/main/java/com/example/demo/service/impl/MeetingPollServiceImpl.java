package com.example.demo.service.impl;

import com.example.demo.dto.CreateMeetingFromPollDTO;
import com.example.demo.dto.MeetingPollDTO;
import com.example.demo.dto.MeetingPollResultDTO;
import com.example.demo.dto.TimeSlotDTO;
import com.example.demo.dto.VoteDTO;
import com.example.demo.entity.Location;
import com.example.demo.entity.MeetingPoll;
import com.example.demo.entity.MeetingPollVote;
import com.example.demo.entity.User;
import com.example.demo.exception.AppError;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.MeetingPollVoteRepository;
import com.example.demo.repository.MeetingPollRepository;
import com.example.demo.service.MeetingPollService;
import com.example.demo.service.MeetingPollTimeSlotService;
import com.example.demo.service.ScheduledPollEventService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

@Service
public class MeetingPollServiceImpl implements MeetingPollService {
    private final MeetingPollRepository meetingPollRepository;
    private final UserService userService;
    private final LocationRepository locationRepository;
    private final MeetingPollTimeSlotService meetingPollTimeSlotService;
    private final MeetingPollVoteRepository meetingPollVoteRepository;

    private final ScheduledPollEventService scheduledPollEventService;

    @Autowired
    public MeetingPollServiceImpl(MeetingPollRepository meetingPollRepository,
                                  UserService userService,
                                  LocationRepository locationRepository,
                                  MeetingPollTimeSlotService meetingPollTimeSlotService,
                                  MeetingPollVoteRepository meetingPollVoteRepository,
                                  ScheduledPollEventService scheduledPollEventService) {
        this.meetingPollRepository = meetingPollRepository;
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.meetingPollTimeSlotService = meetingPollTimeSlotService;
        this.meetingPollVoteRepository = meetingPollVoteRepository;
        this.scheduledPollEventService = scheduledPollEventService;
    }

    @Override
    public ResponseEntity<?> getMeetingPollInfo(Long meetingPollId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<MeetingPoll> meetingPoll = meetingPollRepository.findById(meetingPollId);
        if (meetingPoll.isEmpty()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(),
                            "Meeting poll not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (meetingPoll.get().getUser().getEmail().equals(authentication.getName())) {
            MeetingPollDTO meetingPollDTO = new MeetingPollDTO(
                    meetingPoll.get().getTitle(),
                    meetingPoll.get().getDuration(),
                    meetingPoll.get().getDescription(),
                    meetingPoll.get().getLocation().getId(),
                    meetingPollTimeSlotService.getMeetingPollTimeSlots(meetingPollId));
            return new ResponseEntity<>(meetingPollDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new AppError(HttpStatus.NOT_FOUND.value(),
                        "Access denied"),
                HttpStatus.NOT_FOUND
        );
    }

    @Override
    @Transactional
    public ResponseEntity<?> createMeetingPoll(MeetingPollDTO meetingPollDTO) {
        MeetingPoll meetingPoll = new MeetingPoll(meetingPollDTO);
        meetingPoll.setUser(userService.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null));

        if (meetingPoll.getUser() == null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(),
                            "User not found"),
                    HttpStatus.NOT_FOUND);
        }

        int i = 1;
        while (meetingPollRepository
                .findByTitleAndUserId(meetingPoll.getTitle(), meetingPoll.getUser().getId()).isPresent()) {
            String title = meetingPoll.getTitle();
            if (i > 1) {
                title = title.substring(0, title.length() - 1);
            }
            title += i;
            meetingPoll.setTitle(title);
            ++i;
        }

        Location location = locationRepository.findById(meetingPollDTO.getLocationId()).orElse(null);
        if (location == null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(),
                            "Location not found"),
                    HttpStatus.NOT_FOUND);
        }
        meetingPollDTO.setTitle(meetingPoll.getTitle());
        meetingPoll.setLocation(location);
        meetingPollRepository.save(meetingPoll);

        if (meetingPollTimeSlotService.createMeetingPollTimeSlots(
                meetingPollDTO, meetingPoll.getUser().getId()).getStatusCode() != HttpStatus.CREATED) {
            meetingPollRepository.delete(meetingPoll);
            return new ResponseEntity<>("Time Slots not created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @Override
    @Transactional
    public ResponseEntity<?> deleteMeetingPoll(Long meetingPollId) {
        meetingPollRepository.deleteById(meetingPollId);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllMeetingPolls() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User id = userService.findByEmail(authentication.getName()).orElse(null);
        if (id == null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(),
                            "User not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<MeetingPollDTO> meetingPollDTOS = new ArrayList<>();
        for (MeetingPoll meetingPoll : meetingPollRepository.findAllByUserId(id.getId())) {
            meetingPollDTOS.add(new MeetingPollDTO(meetingPoll.getTitle(),
                    meetingPoll.getDuration(),
                    meetingPoll.getDescription(),
                    meetingPoll.getLocation().getId(),
                    meetingPollTimeSlotService.getMeetingPollTimeSlots(meetingPoll.getId())));
        }
        return new ResponseEntity<>(meetingPollDTOS, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<?> castVote(String userLink, Long meetingPollId, VoteDTO voteDTO) {
        User user = userService.findByLink(userLink)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        MeetingPoll meetingPoll = meetingPollRepository.findById(meetingPollId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting poll not found"));

        if (!meetingPollTimeSlotService.findByMeetingPollIdAndRegisteredEmail(meetingPollId, voteDTO.getRegisteredEmail()).isEmpty()) {
            return new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Already voted"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!meetingPoll.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        for (Long timeSlot : voteDTO.getTimeSlotId()) {
            if (meetingPollTimeSlotService.findByIdAndMeetingPollId(timeSlot, meetingPollId).isEmpty()) {
                return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                        "Time Slot not found"),
                        HttpStatus.NOT_FOUND);
            }
        }

        for (Long timeSlot : voteDTO.getTimeSlotId()) {
            MeetingPollVote meetingPollVote = new MeetingPollVote(
                    meetingPollTimeSlotService.findById(timeSlot).orElse(null),
                    voteDTO.getRegisteredName(),
                    voteDTO.getRegisteredEmail());
            meetingPollVoteRepository.save(meetingPollVote);
        }
        return new ResponseEntity<>("Created", HttpStatus.CREATED);
    }

    @Override
    public List<TimeSlotDTO> getMeetingPollTimeSlots(Long meetingPollId) {
        Object timeSlots = getMeetingPollInfo(meetingPollId).getBody();
        if (timeSlots instanceof MeetingPollDTO) {
            return ((MeetingPollDTO) timeSlots).getTimeSlots();
        }
        return new ArrayList<>();
    }

    @Override
    public List<MeetingPollResultDTO> getMeetingPollResults(Long meetingPollId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<MeetingPoll> meetingPoll = meetingPollRepository.findById(meetingPollId);
        if (meetingPoll.isEmpty()) {
            return new ArrayList<>();
        }

        if (authentication.getName().equals(meetingPoll.get().getUser().getEmail())) {
            return meetingPollVoteRepository.getMeetingPollResults(meetingPollId);
        }
        return new ArrayList<>();
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
}
