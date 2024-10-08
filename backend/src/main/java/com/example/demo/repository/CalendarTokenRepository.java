package com.example.demo.repository;

import com.example.demo.entity.Calendar;
import com.example.demo.entity.CalendarToken;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalendarTokenRepository extends JpaRepository<CalendarToken, Long> {
    Optional<CalendarToken> findByUser(User user);

    Optional<CalendarToken> findByUserAndCalendar(User user, Calendar calendar);
}
