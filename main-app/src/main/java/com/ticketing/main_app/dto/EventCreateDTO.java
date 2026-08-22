package com.ticketing.main_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventCreateDTO {

    @NotBlank(message = "Event title is required.")
    private String title;

    @NotNull(message = "Base price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero.")
    private BigDecimal basePrice;

    @NotNull(message = "Event date is required.")
    @Future(message = "Event date must be in the future.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime eventDate;

    private Long venueId; // Optional if typing a new venue
    private String customVenueName;
    private String customVenueCity;
    private Integer customVenueCapacity;

    public EventCreateDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getCustomVenueName() { return customVenueName; }
    public void setCustomVenueName(String customVenueName) { this.customVenueName = customVenueName; }
    public String getCustomVenueCity() { return customVenueCity; }
    public void setCustomVenueCity(String customVenueCity) { this.customVenueCity = customVenueCity; }
    public Integer getCustomVenueCapacity() { return customVenueCapacity; }
    public void setCustomVenueCapacity(Integer customVenueCapacity) { this.customVenueCapacity = customVenueCapacity; }
}