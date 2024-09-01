package com.example.demo.service;

import com.example.demo.entity.Calendar;
import com.example.demo.entity.Meeting;
import com.example.demo.entity.User;

public interface GoogleCalendarService {
    String createEvent(User user, Meeting meeting);
    void deleteEvent(Calendar calendar, String eventId);
}
