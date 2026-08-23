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
        Venue venue;

        // If user typed custom venue info, save new venue
        if (dto.getCustomVenueName() != null && !dto.getCustomVenueName().isBlank()) {
            String city = (dto.getCustomVenueCity() != null && !dto.getCustomVenueCity().isBlank())
                    ? dto.getCustomVenueCity() : "Sofia";
            int capacity = (dto.getCustomVenueCapacity() != null && dto.getCustomVenueCapacity() > 0)
                    ? dto.getCustomVenueCapacity() : 500;
            venue = venueRepository.save(new Venue(dto.getCustomVenueName().trim(), city, capacity));
        } else if (dto.getVenueId() != null) {
            venue = venueRepository.findById(dto.getVenueId())
                    .orElseThrow(() -> new IllegalArgumentException("Venue not found with ID: " + dto.getVenueId()));
        } else {
            throw new IllegalArgumentException("Please select an existing venue or type a custom venue name.");
        }

        Event event = new Event(dto.getTitle(), dto.getBasePrice(), dto.getEventDate(), venue);
        return eventRepository.save(event);
    }

    public List<Event> searchEvents(String query) {
        if (query == null || query.isBlank()) {
            return getAllEvents();
        }
        return eventRepository.searchEvents(query.trim());
    }

    public Event updateEvent(Long id, EventCreateDTO dto) {
        Event existingEvent = getEventById(id);

        existingEvent.setTitle(dto.getTitle());
        existingEvent.setBasePrice(dto.getBasePrice());
        existingEvent.setEventDate(dto.getEventDate());

        if (dto.getCustomVenueName() != null && !dto.getCustomVenueName().isBlank()) {
            String city = (dto.getCustomVenueCity() != null && !dto.getCustomVenueCity().isBlank())
                    ? dto.getCustomVenueCity() : "Sofia";
            int capacity = (dto.getCustomVenueCapacity() != null && dto.getCustomVenueCapacity() > 0)
                    ? dto.getCustomVenueCapacity() : 500;
            Venue venue = venueRepository.save(new Venue(dto.getCustomVenueName().trim(), city, capacity));
            existingEvent.setVenue(venue);
        } else if (dto.getVenueId() != null) {
            Venue venue = venueRepository.findById(dto.getVenueId())
                    .orElseThrow(() -> new IllegalArgumentException("Venue not found with ID: " + dto.getVenueId()));
            existingEvent.setVenue(venue);
        }

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete: Event not found with ID: " + id);
        }
        eventRepository.deleteById(id);
    }

    public int getRemainingCapacity(Long eventId) {
        Event event = getEventById(eventId);
        int totalCapacity = event.getVenue().getCapacity();
        int soldTickets = event.getTickets() != null ? event.getTickets().size() : 0;
        return Math.max(0, totalCapacity - soldTickets);
    }

    public boolean isSoldOut(Long eventId) {
        return getRemainingCapacity(eventId) <= 0;
    }
}