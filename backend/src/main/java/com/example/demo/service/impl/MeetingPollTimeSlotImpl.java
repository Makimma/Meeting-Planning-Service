package com.example.demo.service.impl;

import com.example.demo.dto.MeetingPollDTO;
import com.example.demo.dto.TimeSlotDTO;
import com.example.demo.entity.MeetingPoll;
import com.example.demo.entity.MeetingPollTimeSlot;
import com.example.demo.exception.AppError;
import com.example.demo.repository.MeetingPollRepository;
import com.example.demo.repository.MeetingPollTimeSlotRepository;
import com.example.demo.service.MeetingPollTimeSlotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingPollTimeSlotImpl implements MeetingPollTimeSlotService {
    private final MeetingPollRepository meetingPollRepository;
    private final MeetingPollTimeSlotRepository meetingPollTimeSlotRepository;

    @Autowired
    public MeetingPollTimeSlotImpl(MeetingPollRepository meetingPollRepository,
                                   MeetingPollTimeSlotRepository meetingPollTimeSlotRepository) {
        this.meetingPollRepository = meetingPollRepository;
        this.meetingPollTimeSlotRepository = meetingPollTimeSlotRepository;
    }

    @Override
    public ResponseEntity<?> createMeetingPollTimeSlots(MeetingPollDTO meetingPollDTO, Long userId) {
        List<TimeSlotDTO> timeSlots = meetingPollDTO.getTimeSlots();
        MeetingPoll meetingPoll = meetingPollRepository.findByTitleAndId(
                        meetingPollDTO.getTitle(), userId)
                .orElse(null);
        if (meetingPoll == null) {
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                    "Meeting poll not found"),
                    HttpStatus.NOT_FOUND);
        }

        for (TimeSlotDTO timeSlot : timeSlots) {
            MeetingPollTimeSlot meetingPollTimeSlot = new MeetingPollTimeSlot();
            //TODO: check that time slot does not overlap

            meetingPollTimeSlot.setMeetingPoll(meetingPoll);
            meetingPollTimeSlot.setBeginAt(timeSlot.getBeginAt());
            meetingPollTimeSlot.setEndAt(timeSlot.getEndAt());

            meetingPollTimeSlotRepository.save(meetingPollTimeSlot);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
