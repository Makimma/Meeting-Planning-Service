package com.example.demo.service.impl;

import com.example.demo.entity.Calendar;
import com.example.demo.entity.CalendarToken;
import com.example.demo.entity.User;
import com.example.demo.exception.CalendarAlreadyConnectedException;
import com.example.demo.exception.CalendarParseCodeException;
import com.example.demo.repository.ConnectedCalendarRepository;
import com.example.demo.service.CalendarService;
import com.example.demo.service.CalendarTokenService;
import com.example.demo.service.OAuthService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OAuthServiceImpl implements OAuthService {
    private final UserService userService;
    private final CalendarService calendarService;
    private final CalendarTokenService calendarTokenService;
    private final ConnectedCalendarRepository connectedCalendarRepository;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Autowired
    public OAuthServiceImpl(UserService userService,
                            CalendarService calendarService,
                            CalendarTokenService calendarTokenService,
                            ConnectedCalendarRepository connectedCalendarRepository) {
        this.userService = userService;
        this.calendarService = calendarService;
        this.calendarTokenService = calendarTokenService;
        this.connectedCalendarRepository = connectedCalendarRepository;
    }

    @Override
    public JsonNode exchangeCodeForTokens(String code) {
        User currentUser = userService.findByEmail(AuthUtils.getCurrentUserEmail());

        Calendar calendar = calendarService.findByName("Google");
        if (connectedCalendarRepository.existsByUserAndCalendar(currentUser, calendar)) {
            throw new CalendarAlreadyConnectedException("Calendar already connected");
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://oauth2.googleapis.com/token");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("code", code));
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            params.add(new BasicNameValuePair("redirect_uri", redirectUri));
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));
            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();

                JsonNode jsonNode = mapper.readTree(responseBody);

                if (jsonNode.has("error")) {
                    String error = jsonNode.get("error").asText();
                    String errorDescription = jsonNode.has("error_description") ? jsonNode.get("error_description").asText() : "No description provided";
                    throw new CalendarParseCodeException("Error fetching tokens: " + error + " - " + errorDescription);
                }

                return jsonNode;
            } catch (ParseException e) {
                throw new CalendarParseCodeException("Error during token exchange");
            }
        } catch (IOException e) {
            throw new CalendarParseCodeException("Error during token exchange");
        }
    }

    @Override
    @Transactional
    public void refreshAccessToken() {
        CalendarToken calendarToken = calendarTokenService
                .findByUser(userService.findByEmail(AuthUtils.getCurrentUserEmail()));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://oauth2.googleapis.com/token");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            params.add(new BasicNameValuePair("refresh_token", calendarToken.getRefreshToken()));
            params.add(new BasicNameValuePair("grant_type", "refresh_token"));
            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(responseBody);

                String newAccessToken = jsonNode.get("access_token").asText();
                long expiresIn = jsonNode.get("expires_in").asLong();
                ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(expiresIn);

                calendarToken.setAccessToken(newAccessToken);
                calendarToken.setExpiresAt(expiresAt);
                calendarTokenService.saveAndFlush(calendarToken);

            } catch (ParseException e) {
                throw new CalendarParseCodeException("Error during token exchange");
            }
        } catch (IOException e) {
            throw new CalendarParseCodeException("Error during token exchange");
        }
    }
}
