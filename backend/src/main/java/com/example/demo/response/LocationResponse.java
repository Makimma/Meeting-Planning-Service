package com.example.demo.response;

import com.example.demo.entity.Location;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationResponse {
    private Long id;
    private String name;

    public LocationResponse(Location location) {
        this.id = location.getId();
        this.name = location.getName();
    }
}
