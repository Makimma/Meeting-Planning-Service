package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.util.AuthUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {
    private final UserService userService;
    private final OAuthService oAuthService;
    private final ConferenceService conferenceService;
    private final CalendarTokenService calendarTokenService;

    @Autowired
    public GoogleCalendarServiceImpl(UserService userService,
                                     OAuthService oAuthService,
                                     ConferenceService conferenceService,
                                     CalendarTokenService calendarTokenService) {
        this.userService = userService;
        this.oAuthService = oAuthService;
        this.conferenceService = conferenceService;
        this.calendarTokenService = calendarTokenService;
    }

    @Override
    public String createEvent(User user, Meeting meeting) {
        String accessToken = getAccessToken(meeting.getCalendar());
        if (accessToken == null) {
            return null;
        }

        // Запрос
        String calendarId = "primary";
        String url = String.format("https://www.googleapis.com/calendar/v3/calendars/%s/events?sendUpdates=all&conferenceDataVersion=1", calendarId);

        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Content-Type", "application/json; charset=UTF-8");

        // Тело запроса
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode event = mapper.createObjectNode();

        event.put("summary", meeting.getTitle());
        event.put("description", meeting.getDescription());
        event.putObject("start").put("dateTime", meeting.getBeginAt().format(formatter));
        event.putObject("end").put("dateTime", meeting.getEndAt().format(formatter));
        event.put("description", meeting.getDescription());

        ArrayNode attendees = event.putArray("attendees");
        for (String email : meeting.getParticipants().stream().map(MeetingParticipant::getParticipantEmail).toList()) {
            ObjectNode attendee = attendees.addObject();
            attendee.put("email", email);
        }

        String conferenceType = meeting.getLocation().getName();
        if ("Google Meet".equalsIgnoreCase(conferenceType)) {
            try {
                String conferenceLink = conferenceService.createConference(meeting);
                event.set("conferenceData", mapper.readTree(conferenceLink));
                event.put("location", meeting.getLocation().getName());
            } catch (JsonProcessingException ignored) {
            }
        } else if ("On-Site".equalsIgnoreCase(conferenceType)) {
            event.put("location", meeting.getPhysicalAddress());
        } /*else if ("Zoom".equalsIgnoreCase(conferenceType)) {
            // TODO: добавить логику для интеграции с Zoom API
        }*/

        StringEntity entity = new StringEntity(event.toString(), StandardCharsets.UTF_8);
        post.setEntity(entity);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            if (response.getCode() != 200) {
                return null;
            }

            String responseBody = EntityUtils.toString(response.getEntity());
            JsonNode jsonNode = mapper.readTree(responseBody);
            meeting.setConferenceLink(jsonNode.path("hangoutLink").asText());

            return jsonNode.get("id").asText();
        } catch (IOException | ParseException e) {
            return null;
        }
    }

    @Override
    public void deleteEvent(Calendar calendar, String eventId) {
        String accessToken = getAccessToken(calendar);
        if (accessToken == null) {
            return;
        }

        String calendarId = "primary";
        String url = String.format("https://www.googleapis.com/calendar/v3/calendars/%s/events/%s", calendarId, eventId);
        HttpDelete delete = new HttpDelete(url);
        delete.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(delete)) {

            if (response.getCode() != 204) { // 204 означает успешное удаление
                String responseBody = EntityUtils.toString(response.getEntity());
                throw new RuntimeException("Failed to delete calendar event. Response: " + responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete event from Google Calendar", e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAccessToken(Calendar calendar) {
        User user = userService.getOptionalByEmail(AuthUtils.getCurrentUserEmail())
                .orElse(null);

        CalendarToken calendarToken = calendarTokenService.getOptionalByUserAndCalendar(user, calendar)
                .orElse(null);

        if (user == null || calendarToken == null) {
            return null;
        }

        if (calendarToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            oAuthService.refreshAccessToken();
        }
        return calendarToken.getAccessToken();
    }
}
