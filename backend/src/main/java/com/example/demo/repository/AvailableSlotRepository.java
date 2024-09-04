package com.example.demo.repository;

import com.example.demo.entity.AvailableSlot;
import com.example.demo.entity.MeetingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, Long> {
    void deleteByStartDateTimeBefore(ZonedDateTime dateTime);

    boolean existsByMeetingTypeAndStartDateTime(MeetingType meetingType, ZonedDateTime startDateTime);

    List<AvailableSlot> findByMeetingTypeIdAndStartDateTimeAfterAndReservedFalse(Long meetingTypeId, ZonedDateTime dateTime);
}
