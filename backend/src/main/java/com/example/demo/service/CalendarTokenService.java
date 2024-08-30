package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface CalendarTokenService {
    void saveTokens(JsonNode tokenData, String provider);
    boolean isUserConnectedToGoogleCalendar();
    void disconnectFromGoogleCalendar();
}
