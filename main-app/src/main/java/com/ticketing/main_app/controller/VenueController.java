package com.ticketing.main_app.controller;

import com.ticketing.main_app.dto.VenueDTO;
import com.ticketing.main_app.model.Venue;
import com.ticketing.main_app.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public String listVenues(Model model) {
        model.addAttribute("venues", venueService.getAllVenues());
        return "venues";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("venueDTO")) {
            model.addAttribute("venueDTO", new VenueDTO());
        }
        return "venue-create";
    }

    @PostMapping("/create")
    public String createVenue(@Valid @ModelAttribute("venueDTO") VenueDTO venueDTO,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "venue-create";
        }
        venueService.createVenue(venueDTO);
        return "redirect:/venues?created=true";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Venue venue = venueService.getVenueById(id);
        model.addAttribute("venueDTO", new VenueDTO(venue.getName(), venue.getCity(), venue.getCapacity()));
        model.addAttribute("venueId", id);
        return "venue-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateVenue(@PathVariable Long id,
                              @Valid @ModelAttribute("venueDTO") VenueDTO venueDTO,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("venueId", id);
            return "venue-edit";
        }
        venueService.updateVenue(id, venueDTO);
        return "redirect:/venues?updated=true";
    }

    @PostMapping("/delete/{id}")
    public String deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return "redirect:/venues?deleted=true";
    }
}