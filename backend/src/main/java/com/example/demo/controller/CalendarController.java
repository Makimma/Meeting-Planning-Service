package com.example.demo.controller;

import com.example.demo.service.CalendarTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarController {
    private final CalendarTokenService calendarTokenService;

    @Autowired
    public CalendarController(CalendarTokenService calendarTokenService) {
        this.calendarTokenService = calendarTokenService;
    }

    @GetMapping("/google-calendar/is-connected")
    public ResponseEntity<Boolean> isUserConnectedToCalendar() {
        return ResponseEntity.ok(calendarTokenService.isUserConnectedToCalendar());
    }
}
