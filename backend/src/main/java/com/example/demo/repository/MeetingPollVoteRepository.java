package com.example.demo.repository;

import com.example.demo.dto.MeetingPollResultDTO;
import com.example.demo.entity.MeetingPollVote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingPollVoteRepository extends JpaRepository<MeetingPollVote, Long> {
    @Query("SELECT mpts FROM MeetingPollVote mpts WHERE mpts.meetingPollTimeSlot.meetingPoll.id = ?1 AND mpts.registeredEmail = ?2")
    List<MeetingPollVote> findByMeetingPollIdAndRegisteredEmail(Long meetingPollId, String email);

    @Query("SELECT new com.example.demo.dto.MeetingPollResultDTO( "
            + "mpts.id, "
            + "COUNT(smpts.meetingPollTimeSlot.id), "
            + "mpts.beginAt, "
            + "mpts.endAt) "
            + "FROM MeetingPollTimeSlot mpts "
            + "LEFT JOIN MeetingPollVote smpts "
            +   "ON mpts.id = smpts.meetingPollTimeSlot.id "
            + "WHERE mpts.meetingPoll.id = ?1 "
            + "GROUP BY mpts.id "
            + "ORDER BY 2 DESC")
    List<MeetingPollResultDTO> getMeetingPollResults(Long meetingPollId);

}
