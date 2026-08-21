package com.ticketing.main_app.service;

import com.ticketing.main_app.dto.EventCreateDTO;
import com.ticketing.main_app.model.Event;
import com.ticketing.main_app.model.Venue;
import com.ticketing.main_app.repository.EventRepository;
import com.ticketing.main_app.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public EventService(EventRepository eventRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Event createEvent(EventCreateDTO dto) {
        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("Venue not found with ID: " + dto.getVenueId()));

        Event event = new Event(dto.getTitle(), dto.getBasePrice(), dto.getEventDate(), venue);
        return eventRepository.save(event);
    }
}