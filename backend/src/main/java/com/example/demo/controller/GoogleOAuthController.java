package com.example.demo.controller;

import com.example.demo.service.CalendarTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/google-calendar")
public class GoogleOAuthController {
    private final CalendarTokenService calendarTokenService;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Autowired
    public GoogleOAuthController(CalendarTokenService calendarTokenService) {
        this.calendarTokenService = calendarTokenService;
    }

    @GetMapping("/connect")
    public void connectGoogleCalendar(HttpServletResponse response) throws IOException {
        String authorizationUrl = "https://accounts.google.com/o/oauth2/auth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/calendar" +
                "&access_type=offline";
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/is-connected")
    public ResponseEntity<Boolean> isUserConnectedToCalendar() {
        return ResponseEntity.ok(calendarTokenService.isUserConnectedToGoogleCalendar());
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<Map<String, String>> disconnectCalendar() {
        calendarTokenService.disconnectFromGoogleCalendar();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Disconnected from Google Calendar");

        return ResponseEntity.ok(response);
    }
}
