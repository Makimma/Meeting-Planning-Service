package com.example.demo.service.impl;

import com.example.demo.entity.Calendar;
import com.example.demo.exception.CalendarNotFoundException;
import com.example.demo.repository.CalendarRepository;
import com.example.demo.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;

    @Autowired
    public CalendarServiceImpl(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    @Override
    public Calendar findByName(String name) {
        return calendarRepository.findByName(name)
                .orElseThrow(() -> new CalendarNotFoundException("Calendar not found"));
    }
}
