package com.example.demo.repository;

import com.example.demo.entity.ConnectedCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectedCalendarRepository extends JpaRepository<ConnectedCalendar, Long> {
}
