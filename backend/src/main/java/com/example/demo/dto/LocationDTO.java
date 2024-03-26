package com.example.demo.dto;

import com.example.demo.entity.Location;

import lombok.Data;

@Data
public class LocationDTO {
    private Long id;
    private String name;

    public LocationDTO(Location location) {
        this.id = location.getId();
        this.name = location.getName();
    }
}
