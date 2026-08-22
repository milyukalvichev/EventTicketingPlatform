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
}