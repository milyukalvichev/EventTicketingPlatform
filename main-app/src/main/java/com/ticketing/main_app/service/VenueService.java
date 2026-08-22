package com.ticketing.main_app.service;

import com.ticketing.main_app.dto.VenueDTO;
import com.ticketing.main_app.model.Venue;
import com.ticketing.main_app.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found with ID: " + id));
    }

    public Venue createVenue(VenueDTO dto) {
        Venue venue = new Venue(dto.getName().trim(), dto.getCity().trim(), dto.getCapacity());
        return venueRepository.save(venue);
    }

    public Venue updateVenue(Long id, VenueDTO dto) {
        Venue venue = getVenueById(id);
        venue.setName(dto.getName().trim());
        venue.setCity(dto.getCity().trim());
        venue.setCapacity(dto.getCapacity());
        return venueRepository.save(venue);
    }

    public void deleteVenue(Long id) {
        Venue venue = getVenueById(id);
        if (!venue.getEvents().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete venue because events are scheduled there.");
        }
        venueRepository.delete(venue);
    }
}