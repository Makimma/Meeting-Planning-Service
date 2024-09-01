package com.example.demo.service.impl;

import com.example.demo.entity.Calendar;
import com.example.demo.entity.CalendarToken;
import com.example.demo.entity.ConnectedCalendar;
import com.example.demo.entity.User;
import com.example.demo.exception.CalendarTokenNotFoundException;
import com.example.demo.repository.CalendarTokenRepository;
import com.example.demo.repository.ConnectedCalendarRepository;
import com.example.demo.service.CalendarService;
import com.example.demo.service.CalendarTokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthUtils;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class CalendarTokenServiceImpl implements CalendarTokenService {
    private final UserService userService;
    private final CalendarService calendarService;
    private final CalendarTokenRepository calendarTokenRepository;
    private final ConnectedCalendarRepository connectedCalendarRepository;

    @Autowired
    public CalendarTokenServiceImpl(CalendarTokenRepository calendarTokenRepository,
                                    UserService userService,
                                    ConnectedCalendarRepository connectedCalendarRepository,
                                    CalendarService calendarService) {
        this.userService = userService;
        this.calendarService = calendarService;
        this.calendarTokenRepository = calendarTokenRepository;
        this.connectedCalendarRepository = connectedCalendarRepository;
    }

    @Override
    public Optional<CalendarToken> getOptionalByUserAndCalendar(User user, Calendar calendar) {
        return calendarTokenRepository.findByUserAndCalendar(user, calendar);
    }

    @Override
    public CalendarToken findByUser(User user) {
        return calendarTokenRepository.findByUser(user)
                .orElseThrow(() -> new CalendarTokenNotFoundException("Calendar Token not found"));
    }

    @Override
    public CalendarToken saveAndFlush(CalendarToken calendarToken) {
        return calendarTokenRepository.saveAndFlush(calendarToken);
    }

    @Override
    @Transactional
    public void saveTokens(JsonNode tokenData, String provider) {
        String accessToken = tokenData.get("access_token").asText();
        String refreshToken;
        if (tokenData.has("refresh_token")) {
            refreshToken = tokenData.get("refresh_token").asText();
        } else {
            refreshToken = "";
        }

        long expiresIn = tokenData.get("expires_in").asLong();
        ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(expiresIn);

        User currentUser = userService.findByEmail(AuthUtils.getCurrentUserEmail());
        Calendar calendar = calendarService.findByName("Google");

        CalendarToken calendarToken = new CalendarToken();
        if (refreshToken.isEmpty()) {
            calendarToken = findByUser(currentUser);
        }
        calendarToken.setUser(currentUser);
        calendarToken.setCalendar(calendar);
        calendarToken.setAccessToken(accessToken);
        calendarToken.setExpiresAt(expiresAt);
        if (!refreshToken.isEmpty()) {
            calendarToken.setRefreshToken(refreshToken);
        }
        calendarTokenRepository.save(calendarToken);

        ConnectedCalendar connectedCalendar = new ConnectedCalendar();
        connectedCalendar.setCalendar(calendar);
        connectedCalendar.setUser(currentUser);
        connectedCalendarRepository.save(connectedCalendar);
    }

    @Override
    public boolean isUserConnectedToGoogleCalendar() {
        User currentUser = userService.findByEmail(AuthUtils.getCurrentUserEmail());
        Calendar calendar = calendarService.findByName("Google");
        return connectedCalendarRepository.existsByUserAndCalendar(currentUser, calendar);
    }

    @Override
    @Transactional
    public void disconnectFromGoogleCalendar() {
        User user = userService.findByEmail(AuthUtils.getCurrentUserEmail());
        Calendar calendar = calendarService.findByName("Google");
        connectedCalendarRepository.deleteByUserAndCalendar(user, calendar);
    }
}
