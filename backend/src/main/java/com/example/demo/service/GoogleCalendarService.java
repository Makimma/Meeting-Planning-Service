package com.example.demo.service;

import com.example.demo.entity.Meeting;
import com.example.demo.entity.User;

public interface GoogleCalendarService {
    String createCalendarEvent(User user, Meeting meeting);
}
