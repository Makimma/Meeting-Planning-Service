package com.example.demo.service;

import com.example.demo.entity.Calendar;
import com.example.demo.entity.CalendarToken;
import com.example.demo.entity.User;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public interface CalendarTokenService {
    Optional<CalendarToken> getOptionalByUserAndCalendar(User user, Calendar calendar);

    CalendarToken findByUser(User user);

    CalendarToken saveAndFlush(CalendarToken calendarToken);

    void saveTokens(JsonNode tokenData, String provider);

    void disconnectFromGoogleCalendar();

    boolean isUserConnectedToGoogleCalendar();
}
