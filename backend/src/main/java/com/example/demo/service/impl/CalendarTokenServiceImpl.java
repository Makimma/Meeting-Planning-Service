package com.example.demo.service.impl;

import com.example.demo.entity.Calendar;
import com.example.demo.entity.CalendarToken;
import com.example.demo.entity.ConnectedCalendar;
import com.example.demo.entity.User;
import com.example.demo.exception.CalendarNotFoundException;
import com.example.demo.repository.CalendarRepository;
import com.example.demo.repository.CalendarTokenRepository;
import com.example.demo.repository.ConnectedCalendarRepository;
import com.example.demo.service.CalendarTokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthUtils;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CalendarTokenServiceImpl implements CalendarTokenService {
    private final CalendarTokenRepository calendarTokenRepository;
    private final UserService userService;
    private final CalendarRepository calendarRepository;
    private final ConnectedCalendarRepository connectedCalendarRepository;

    @Autowired
    public CalendarTokenServiceImpl(CalendarTokenRepository calendarTokenRepository,
                                    UserService userService,
                                    CalendarRepository calendarRepository,
                                    ConnectedCalendarRepository connectedCalendarRepository) {
        this.calendarTokenRepository = calendarTokenRepository;
        this.userService = userService;
        this.calendarRepository = calendarRepository;
        this.connectedCalendarRepository = connectedCalendarRepository;
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

        User currentUser = userService.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Calendar calendar = calendarRepository.findByName("Google")
                .orElseThrow(() -> new CalendarNotFoundException("Calendar not found"));

        CalendarToken calendarToken = new CalendarToken();
        if (refreshToken.isEmpty()) {
            calendarToken = calendarTokenRepository.findByUser(currentUser)
                    .orElseThrow(() -> new CalendarNotFoundException("Calendar not found"));
        }
        calendarToken.setUser(currentUser);
        calendarToken.setCalendar(calendar);
        calendarToken.setAccessToken(accessToken);
        if (!refreshToken.isEmpty()) {
            calendarToken.setRefreshToken(refreshToken);
        }
        calendarToken.setExpiresAt(expiresAt);
        calendarTokenRepository.save(calendarToken);

        ConnectedCalendar connectedCalendar = new ConnectedCalendar();
        connectedCalendar.setCalendar(calendar);
        connectedCalendar.setUser(currentUser);
        connectedCalendarRepository.save(connectedCalendar);
    }

    @Override
    public boolean isUserConnectedToGoogleCalendar() {
        User currentUser = userService.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Calendar calendar = calendarRepository.findByName("Google")
                .orElseThrow(() -> new CalendarNotFoundException("Calendar not found"));

        return connectedCalendarRepository.existsByUserAndCalendar(currentUser, calendar);
    }

    @Override
    @Transactional
    public void disconnectFromGoogleCalendar() {
        User user = userService.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Calendar calendar = calendarRepository.findByName("Google")
                .orElseThrow(() -> new CalendarNotFoundException("Calendar not found"));

        connectedCalendarRepository.deleteByUserAndCalendar(user, calendar);
    }
}
