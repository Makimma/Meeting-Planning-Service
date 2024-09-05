package com.example.demo.repository;

import com.example.demo.entity.MeetingType;
import com.example.demo.entity.MeetingTypeTimeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface MeetingTypeTimeRangeRepository extends JpaRepository<MeetingTypeTimeRange, Long> {
    List<MeetingTypeTimeRange> findByMeetingTypeAndDayOfWeek(MeetingType meetingType, DayOfWeek dayOfWeek);
}
