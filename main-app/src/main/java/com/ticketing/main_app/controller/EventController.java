package com.ticketing.main_app.controller;

import com.ticketing.main_app.dto.EventCreateDTO;
import com.ticketing.main_app.model.Event;
import com.ticketing.main_app.service.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("events", eventService.searchEvents(search));
        model.addAttribute("searchQuery", search != null ? search : "");
        return "events";
    }

    @GetMapping("/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "event-details";
    }

    @GetMapping("/new")
    public String showCreateEventForm(Model model) {
        if (!model.containsAttribute("eventCreateDTO")) {
            model.addAttribute("eventCreateDTO", new EventCreateDTO());
        }
        model.addAttribute("venues", eventService.getAllVenues());
        return "event-create";
    }

    @PostMapping("/create")
    public String createEvent(@Valid @ModelAttribute("eventCreateDTO") EventCreateDTO eventCreateDTO,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("venues", eventService.getAllVenues());
            return "event-create";
        }

        eventService.createEvent(eventCreateDTO);
        return "redirect:/events?created=true";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);

        EventCreateDTO dto = new EventCreateDTO();
        dto.setTitle(event.getTitle());
        dto.setBasePrice(event.getBasePrice());
        dto.setEventDate(event.getEventDate());
        dto.setVenueId(event.getVenue().getId());

        model.addAttribute("eventCreateDTO", dto);
        model.addAttribute("eventId", id);
        model.addAttribute("venues", eventService.getAllVenues());
        return "event-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("eventCreateDTO") EventCreateDTO eventCreateDTO,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventId", id);
            model.addAttribute("venues", eventService.getAllVenues());
            return "event-edit";
        }

        eventService.updateEvent(id, eventCreateDTO);
        return "redirect:/events?updated=true";
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events?deleted=true";
    }
}