package com.example.demo.controller;

import com.example.demo.service.CalendarTokenService;
import com.example.demo.service.OAuthService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/oauth2")
public class OAuthCallbackController {
    private final OAuthService oAuthService;
    private final CalendarTokenService calendarTokenService;

    @Autowired
    public OAuthCallbackController(OAuthService oAuthService,
                                   CalendarTokenService calendarTokenService) {
        this.oAuthService = oAuthService;
        this.calendarTokenService = calendarTokenService;
    }

    @PostMapping("/google-calendar/callback")
    public ResponseEntity<Map<String, String>> oauthCallback(@RequestParam("code") String code) {
        JsonNode tokenData = oAuthService.exchangeCodeForTokens(code);
        calendarTokenService.saveTokens(tokenData, "google");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Tokens saved successfully");

        return ResponseEntity.ok(response);
    }

    //TODO удалить
    @PostMapping("/google-calendar/refresh-access")
    public ResponseEntity<Map<String, String>> refreshAccess() {
        oAuthService.refreshAccessToken();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Tokens saved successfully");

        return ResponseEntity.ok(response);
    }
}
