package com.example.demo.repository;

import com.example.demo.entity.Meeting;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByUser(User user);

    //Upcoming
    List<Meeting> findMeetingsByBeginAtAfter(ZonedDateTime beginAt);

    //Past
    List<Meeting> findMeetingsByEndAtBefore(ZonedDateTime endAt);
}
