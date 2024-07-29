//package com.example.demo.service.impl;
//
//import com.example.demo.dto.MeetingPollDTO;
//import com.example.demo.dto.TimeSlotDTO;
//import com.example.demo.entity.MeetingPoll;
//import com.example.demo.entity.MeetingPollTimeSlot;
//import com.example.demo.entity.MeetingPollVote;
//import com.example.demo.repository.MeetingPollRepository;
//import com.example.demo.repository.MeetingPollTimeSlotRepository;
//import com.example.demo.repository.MeetingPollVoteRepository;
//import com.example.demo.service.MeetingPollTimeSlotService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import jakarta.transaction.Transactional;
//
//@Service
//public class MeetingPollTimeSlotServiceImpl implements MeetingPollTimeSlotService {
//    private final MeetingPollRepository meetingPollRepository;
//    private final MeetingPollTimeSlotRepository meetingPollTimeSlotRepository;
//
//    private final MeetingPollVoteRepository meetingPollVoteRepository;
//
//    @Autowired
//    public MeetingPollTimeSlotServiceImpl(MeetingPollRepository meetingPollRepository,
//                                          MeetingPollTimeSlotRepository meetingPollTimeSlotRepository, MeetingPollVoteRepository meetingPollVoteRepository) {
//        this.meetingPollRepository = meetingPollRepository;
//        this.meetingPollTimeSlotRepository = meetingPollTimeSlotRepository;
//        this.meetingPollVoteRepository = meetingPollVoteRepository;
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> createMeetingPollTimeSlots(MeetingPollDTO meetingPollDTO,
//                                                        Long userId) {
//        List<TimeSlotDTO> timeSlots = meetingPollDTO.getTimeSlots().stream().distinct().toList();
//        MeetingPoll meetingPoll = meetingPollRepository.findByTitleAndUserId(
//                        meetingPollDTO.getTitle(), userId)
//                .orElse(null);
//        if (meetingPoll == null) {
//            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
//                    "Meeting poll not found"),
//                    HttpStatus.NOT_FOUND);
//        }
//        for (TimeSlotDTO timeSlot : timeSlots) {
//            MeetingPollTimeSlot meetingPollTimeSlot = new MeetingPollTimeSlot();
//            if (timeSlot.getBeginAt().isAfter(timeSlot.getEndAt())
//                    || timeSlot.getBeginAt().isBefore(LocalDateTime.now())
//                    || Duration.between(timeSlot.getBeginAt(), timeSlot.getEndAt()).toMinutes() != meetingPollDTO.getDuration()) {
//                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
//                        "Begin time cannot be after end time"),
//                        HttpStatus.BAD_REQUEST);
//            }
//
//            meetingPollTimeSlot.setMeetingPoll(meetingPoll);
//            meetingPollTimeSlot.setBeginAt(timeSlot.getBeginAt());
//            meetingPollTimeSlot.setEndAt(timeSlot.getEndAt());
//            meetingPollTimeSlotRepository.save(meetingPollTimeSlot);
//        }
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//
//    @Override
//    public List<TimeSlotDTO> getMeetingPollTimeSlots(Long meetingPollId) {
//        MeetingPoll meetingPoll = meetingPollRepository.findById(meetingPollId)
//                .orElse(null);
//
//        List<MeetingPollTimeSlot> meetingPollTimeSlots = new ArrayList<>();
//        if (meetingPoll != null) {
//            meetingPollTimeSlots = meetingPollTimeSlotRepository
//                    .findByMeetingPollId(meetingPoll.getId());
//        }
//
//        List<TimeSlotDTO> timeSlotDTOs = new ArrayList<>();
//        for (MeetingPollTimeSlot meetingPollTimeSlot : meetingPollTimeSlots) {
//            TimeSlotDTO timeSlotDTO = new TimeSlotDTO();
//            timeSlotDTO.setId(meetingPollTimeSlot.getId());
//            timeSlotDTO.setBeginAt(meetingPollTimeSlot.getBeginAt());
//            timeSlotDTO.setEndAt(meetingPollTimeSlot.getEndAt());
//            timeSlotDTOs.add(timeSlotDTO);
//        }
//        return timeSlotDTOs;
//    }
//
//    @Override
//    public Optional<MeetingPollTimeSlot> findById(Long timeSlot) {
//        return meetingPollTimeSlotRepository.findById(timeSlot);
//    }
//
//    @Override
//    public Optional<MeetingPollTimeSlot> findByIdAndMeetingPollId(Long timeSlot, Long meetingPollId) {
//        return meetingPollTimeSlotRepository.findByIdAndMeetingPollId(timeSlot, meetingPollId);
//    }
//
//    @Override
//    public List<MeetingPollVote> findByMeetingPollIdAndRegisteredEmail(Long meetingPollId, String email) {
//        return meetingPollVoteRepository.findByMeetingPollIdAndRegisteredEmail(meetingPollId, email);
//    }
//
//}
