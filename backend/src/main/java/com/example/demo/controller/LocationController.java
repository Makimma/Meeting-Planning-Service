package com.example.demo.controller;

import com.example.demo.service.LocationService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("api/v1/locations")
public class LocationController {
    private final LocationService locationService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LocationController(LocationService locationService,
                              JdbcTemplate jdbcTemplate) {
        this.locationService = locationService;
        this.jdbcTemplate = jdbcTemplate;
    }

    //TODO разкомментить
    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("DML/location.sql");
        String sql = Files.readString(Path.of(resource.getURI()));

        jdbcTemplate.execute(sql);
    }

    //FIXME переделать
    @GetMapping
    public ResponseEntity<?> getAllLocations() {
        return new ResponseEntity<>(locationService.getAllLocations(), HttpStatus.OK);
    }
}
