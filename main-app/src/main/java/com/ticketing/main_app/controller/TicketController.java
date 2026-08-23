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

import java.math.BigDecimal;
import java.util.List;

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
                               @RequestParam(defaultValue = "1") int quantity,
                               Model model) {
        Event event = eventService.getEventById(eventId);
        int remaining = eventService.getRemainingCapacity(eventId);

        if (remaining <= 0) {
            return "redirect:/events/" + eventId + "?soldout=true";
        }

        int maxSelectable = Math.min(10, remaining);
        int validQty = Math.max(1, Math.min(maxSelectable, quantity));

        TicketService.PromoEvaluationResult eval = ticketService.evaluatePromo(event.getBasePrice(), promoCode);

        BigDecimal unitPrice = eval.finalPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(validQty));
        BigDecimal totalOriginalPrice = event.getBasePrice().multiply(BigDecimal.valueOf(validQty));
        BigDecimal totalDiscount = totalOriginalPrice.subtract(totalPrice);

        model.addAttribute("event", event);
        model.addAttribute("remainingCapacity", remaining);
        model.addAttribute("maxSelectable", maxSelectable);
        model.addAttribute("promoCode", promoCode != null ? promoCode : "");
        model.addAttribute("quantity", validQty);
        model.addAttribute("unitPrice", unitPrice);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalOriginalPrice", totalOriginalPrice);
        model.addAttribute("totalDiscount", totalDiscount);
        model.addAttribute("promoValid", eval.valid());
        model.addAttribute("promoMessage", eval.message());
        return "checkout";
    }

    @PostMapping("/purchase")
    public String purchaseTicket(@RequestParam Long eventId,
                                 @RequestParam(required = false) String promoCode,
                                 @RequestParam(defaultValue = "1") int quantity,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        List<Ticket> tickets = ticketService.purchaseTickets(userDetails.getUsername(), eventId, promoCode, quantity);
        model.addAttribute("tickets", tickets);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPaid", tickets.stream().map(Ticket::getFinalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        return "ticket-success";
    }

    @PostMapping("/return/{id}")
    public String returnTicket(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        ticketService.returnTicket(id, userDetails.getUsername());
        return "redirect:/tickets/my-tickets?returned=true";
    }

    @GetMapping("/my-tickets")
    public String userTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("tickets", ticketService.getUserTickets(userDetails.getUsername()));
        return "my-tickets";
    }
}