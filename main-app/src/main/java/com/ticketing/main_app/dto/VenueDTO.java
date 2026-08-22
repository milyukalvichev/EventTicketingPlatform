package com.ticketing.main_app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VenueDTO {

    @NotBlank(message = "Venue name is required.")
    private String name;

    @NotBlank(message = "City is required.")
    private String city;

    @NotNull(message = "Capacity is required.")
    @Min(value = 10, message = "Capacity must be at least 10.")
    private Integer capacity;

    public VenueDTO() {}

    public VenueDTO(String name, String city, Integer capacity) {
        this.name = name;
        this.city = city;
        this.capacity = capacity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}