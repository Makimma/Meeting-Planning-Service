package com.example.demo.service.impl;

import com.example.demo.entity.Meeting;
import com.example.demo.service.ConferenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class GoogleMeetConferenceServiceImpl implements ConferenceService {
    @Override
    public String createConference(Meeting meeting) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode conferenceData = mapper.createObjectNode();
        ObjectNode createRequest = conferenceData.putObject("createRequest");
        ObjectNode conferenceSolutionKey = createRequest.putObject("conferenceSolutionKey");
        conferenceSolutionKey.put("type", "hangoutsMeet");  // Указываем тип конференции Google Meet
        createRequest.put("requestId", java.util.UUID.randomUUID().toString());  // Уникальный идентификатор запроса

        return conferenceData.toString();
    }
}
