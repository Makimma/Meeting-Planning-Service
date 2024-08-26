package com.example.demo.repository;

import com.example.demo.entity.Calendar;
import com.example.demo.entity.ConnectedCalendar;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectedCalendarRepository extends JpaRepository<ConnectedCalendar, Long> {
    boolean existsByUserAndCalendar(User user, Calendar calendar);
}
