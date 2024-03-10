package com.example.demo.service.impl;

import com.example.demo.dto.MeetingPollDTO;
import com.example.demo.entity.Location;
import com.example.demo.entity.MeetingPoll;
import com.example.demo.exception.AppError;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.MeetingPollRepository;
import com.example.demo.service.MeetingPollService;
import com.example.demo.service.MeetingPollTimeSlotService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

@Service
public class MeetingPollServiceImpl implements MeetingPollService {
    private final MeetingPollRepository meetingPollRepository;
    private final UserService userService;
    private final LocationRepository locationRepository;
    private final MeetingPollTimeSlotService meetingPollTimeSlotService;

    @Autowired
    public MeetingPollServiceImpl(MeetingPollRepository meetingPollRepository,
                                  UserService userService,
                                  LocationRepository locationRepository,
                                  MeetingPollTimeSlotService meetingPollTimeSlotService) {
        this.meetingPollRepository = meetingPollRepository;
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.meetingPollTimeSlotService = meetingPollTimeSlotService;
    }

    @Override
    public ResponseEntity<?> getMeetingPollInfo(Long meetingPollId) {
        
        return null;
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
                .findByTitleAndId(meetingPoll.getTitle(), meetingPoll.getUser().getId()).isPresent()) {
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

        meetingPoll.setLocation(location);
        meetingPollRepository.save(meetingPoll);

        if (meetingPollTimeSlotService.createMeetingPollTimeSlots(
                meetingPollDTO, meetingPoll.getUser().getId()).getStatusCode() != HttpStatus.CREATED) {
            //TODO: delete poll
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getAllMeetingPoll() {
        return null;
    }
}
