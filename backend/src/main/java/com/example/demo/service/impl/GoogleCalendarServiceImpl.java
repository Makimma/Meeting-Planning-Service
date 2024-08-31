package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.exception.CalendarNotFoundException;
import com.example.demo.repository.CalendarTokenRepository;
import com.example.demo.repository.ConnectedCalendarRepository;
import com.example.demo.service.GoogleCalendarService;
import com.example.demo.service.OAuthService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {
    private final OAuthService oAuthService;
    private final CalendarTokenRepository calendarTokenRepository;
    private final ConnectedCalendarRepository connectedCalendarRepository;
    private final UserService userService;

    @Autowired
    public GoogleCalendarServiceImpl(OAuthService oAuthService,
                                     CalendarTokenRepository calendarTokenRepository,
                                     ConnectedCalendarRepository connectedCalendarRepository,
                                     UserService userService) {
        this.oAuthService = oAuthService;
        this.calendarTokenRepository = calendarTokenRepository;
        this.connectedCalendarRepository = connectedCalendarRepository;
        this.userService = userService;
    }

    @Override
    public String createCalendarEvent(User user, Meeting meeting) {
        CalendarToken calendarToken = calendarTokenRepository.findByUserAndCalendar(user, meeting.getCalendar())
                .orElse(null);

        if (calendarToken == null || !connectedCalendarRepository.existsByUserAndCalendar(user, meeting.getCalendar())) {
            return null;
        }

        if (calendarToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            oAuthService.refreshAccessToken();
        }
        String accessToken = calendarToken.getAccessToken();

        // Запрос
        String calendarId = "primary";
        String url = String.format("https://www.googleapis.com/calendar/v3/calendars/%s/events?sendUpdates=all", calendarId);

        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Content-Type", "application/json");

        // Тело запроса
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode event = mapper.createObjectNode();

        event.put("summary", meeting.getTitle());
        //TODO ссылка на встречу event.put("description", eventRequest.getDescription() + "\nMeeting link: " + meetingLink);
        event.put("description", meeting.getDescription());
        event.putObject("start").put("dateTime", meeting.getBeginAt().format(formatter));
        event.putObject("end").put("dateTime", meeting.getEndAt().format(formatter));
        event.put("location", meeting.getLocation().getName());

        ArrayNode attendees = event.putArray("attendees");
        for (String email : meeting.getParticipants().stream().map(MeetingParticipant::getParticipantEmail).toList()) {
            ObjectNode attendee = attendees.addObject();
            attendee.put("email", email);
        }

        StringEntity entity = new StringEntity(event.toString());
        post.setEntity(entity);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            if (response.getCode() != 200) {
                return null;
            }

            String responseBody = EntityUtils.toString(response.getEntity());
            JsonNode jsonNode = mapper.readTree(responseBody);

            return jsonNode.get("id").asText();
        } catch (IOException | ParseException ignored) {
            return null;
        }
    }

    @Override
    public void deleteEventFromCalendar(Calendar calendar, String eventId) {
        User user = userService.findByEmail(AuthUtils.getCurrentUserEmail())
                .orElse(null);

        CalendarToken calendarToken = calendarTokenRepository.findByUserAndCalendar(user, calendar)
                .orElse(null);

        if (user == null || calendarToken == null) {
            return;
        }

        if (calendarToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            oAuthService.refreshAccessToken();
        }
        String accessToken = calendarToken.getAccessToken();

        String url = String.format("https://www.googleapis.com/calendar/v3/calendars/%s/events/%s", "primary", eventId);
        HttpDelete delete = new HttpDelete(url);
        delete.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(delete)) {

            if (response.getCode() != 204) { // 204 No Content означает успешное удаление
                String responseBody = EntityUtils.toString(response.getEntity());
                throw new RuntimeException("Failed to delete calendar event. Response: " + responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete event from Google Calendar", e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
