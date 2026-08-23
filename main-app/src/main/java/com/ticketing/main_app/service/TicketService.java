package com.ticketing.main_app.service;

import com.ticketing.main_app.dto.PromoResponseDTO;
import com.ticketing.main_app.model.Event;
import com.ticketing.main_app.model.Ticket;
import com.ticketing.main_app.model.User;
import com.ticketing.main_app.repository.EventRepository;
import com.ticketing.main_app.repository.TicketRepository;
import com.ticketing.main_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RestClient pricingRestClient;

    public TicketService(TicketRepository ticketRepository,
                         EventRepository eventRepository,
                         UserRepository userRepository,
                         RestClient pricingRestClient) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.pricingRestClient = pricingRestClient;
    }

    public PromoEvaluationResult evaluatePromo(BigDecimal basePrice, String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return new PromoEvaluationResult(basePrice, false, null);
        }

        try {
            PromoResponseDTO response = pricingRestClient.get()
                    .uri("/api/promos/validate?code=" + promoCode.trim())
                    .retrieve()
                    .body(PromoResponseDTO.class);

            if (response != null && response.isValid()) {
                BigDecimal factor = response.getDiscountPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal discount = basePrice.multiply(factor);
                BigDecimal finalPrice = basePrice.subtract(discount).setScale(2, RoundingMode.HALF_UP);
                return new PromoEvaluationResult(finalPrice, true, response.getMessage());
            } else if (response != null) {
                return new PromoEvaluationResult(basePrice, false, response.getMessage());
            }
        } catch (Exception e) {
            return new PromoEvaluationResult(basePrice, false, "Pricing service unavailable. Base price applied.");
        }

        return new PromoEvaluationResult(basePrice, false, "Invalid promotional code.");
    }

    public List<Ticket> purchaseTickets(String username, Long eventId, String promoCode, int quantity) {
        if (quantity < 1 || quantity > 10) {
            throw new IllegalArgumentException("Quantity must be between 1 and 10 tickets.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        // Calculate real-time remaining capacity
        int totalCapacity = event.getVenue().getCapacity();
        int currentSold = event.getTickets() != null ? event.getTickets().size() : 0;
        int remainingCapacity = totalCapacity - currentSold;

        if (quantity > remainingCapacity) {
            throw new IllegalArgumentException(
                    remainingCapacity <= 0
                            ? "This event is completely SOLD OUT."
                            : "Only " + remainingCapacity + " ticket(s) remaining for this event."
            );
        }

        PromoEvaluationResult result = evaluatePromo(event.getBasePrice(), promoCode);

        List<Ticket> purchasedTickets = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = new Ticket(
                    result.finalPrice(),
                    result.valid() ? promoCode.trim().toUpperCase() : null,
                    LocalDateTime.now(),
                    user,
                    event
            );
            purchasedTickets.add(ticketRepository.save(ticket));
        }

        return purchasedTickets;
    }

    public List<Ticket> getUserTickets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return ticketRepository.findByUserIdOrderByPurchaseDateDesc(user.getId());
    }

    public record PromoEvaluationResult(BigDecimal finalPrice, boolean valid, String message) {}

    public void returnTicket(Long ticketId, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + ticketId));

        if (!ticket.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not authorized to return this ticket.");
        }

        ticketRepository.delete(ticket);
    }
}