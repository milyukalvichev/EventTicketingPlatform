package com.ticketing.main_app.controller;

import com.ticketing.main_app.model.Event;
import com.ticketing.main_app.model.Ticket;
import com.ticketing.main_app.service.EventService;
import com.ticketing.main_app.service.TicketService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final EventService eventService;

    public TicketController(TicketService ticketService, EventService eventService) {
        this.ticketService = ticketService;
        this.eventService = eventService;
    }

    @GetMapping("/checkout/{eventId}")
    public String checkoutPage(@PathVariable Long eventId,
                               @RequestParam(required = false) String promoCode,
                               Model model) {
        Event event = eventService.getEventById(eventId);
        TicketService.PromoEvaluationResult eval = ticketService.evaluatePromo(event.getBasePrice(), promoCode);

        model.addAttribute("event", event);
        model.addAttribute("promoCode", promoCode != null ? promoCode : "");
        model.addAttribute("finalPrice", eval.finalPrice());
        model.addAttribute("promoValid", eval.valid());
        model.addAttribute("promoMessage", eval.message());
        return "checkout";
    }

    @PostMapping("/purchase")
    public String purchaseTicket(@RequestParam Long eventId,
                                 @RequestParam(required = false) String promoCode,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        Ticket ticket = ticketService.purchaseTicket(userDetails.getUsername(), eventId, promoCode);
        model.addAttribute("ticket", ticket);
        return "ticket-success";
    }

    @GetMapping("/my-tickets")
    public String userTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("tickets", ticketService.getUserTickets(userDetails.getUsername()));
        return "my-tickets";
    }
}