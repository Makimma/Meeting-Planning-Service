package com.example.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/api/v1/google-calendar")
public class GoogleOAuthController {
    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @GetMapping("/connect-google-calendar")
    public void connectGoogleCalendar(HttpServletResponse response) throws IOException {
        String authorizationUrl = "https://accounts.google.com/o/oauth2/auth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/calendar" +
                "&access_type=offline";
        response.sendRedirect(authorizationUrl);
    }
}
