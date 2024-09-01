package com.example.demo.service.impl;

import com.example.demo.entity.Location;
import com.example.demo.exception.LocationNotFoundException;
import com.example.demo.response.LocationResponse;
import com.example.demo.repository.LocationRepository;
import com.example.demo.service.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream().map(LocationResponse::new).toList();
    }

    @Override
    public Location findById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));
    }
}
