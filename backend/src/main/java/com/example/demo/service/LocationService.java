package com.example.demo.service;

import com.example.demo.entity.Location;
import com.example.demo.response.LocationResponse;

import java.util.List;

public interface LocationService {
    List<LocationResponse> getAllLocations();
    Location findById(Long locationId);
}
