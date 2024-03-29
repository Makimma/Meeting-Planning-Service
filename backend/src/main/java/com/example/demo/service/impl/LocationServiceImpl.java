package com.example.demo.service.impl;

import com.example.demo.dto.LocationDTO;
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
    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream().map(LocationDTO::new).toList();
    }
}
