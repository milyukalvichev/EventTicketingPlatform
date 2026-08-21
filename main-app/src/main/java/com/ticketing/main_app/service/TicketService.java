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

    public Ticket purchaseTicket(String username, Long eventId, String promoCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        BigDecimal finalPrice = calculateFinalPrice(event.getBasePrice(), promoCode);

        Ticket ticket = new Ticket(
                finalPrice,
                (promoCode != null && !promoCode.isBlank()) ? promoCode.trim().toUpperCase() : null,
                LocalDateTime.now(),
                user,
                event
        );

        return ticketRepository.save(ticket);
    }

    public BigDecimal calculateFinalPrice(BigDecimal basePrice, String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return basePrice;
        }

        try {
            PromoResponseDTO response = pricingRestClient.get()
                    .uri("/api/promos/validate?code=" + promoCode.trim())
                    .retrieve()
                    .body(PromoResponseDTO.class);

            if (response != null && response.isValid()) {
                BigDecimal discountPercentage = response.getDiscountPercentage();
                BigDecimal discountFactor = discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal discountAmount = basePrice.multiply(discountFactor);
                return basePrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            // Fallback: charge base price if pricing microservice is temporarily unavailable
            return basePrice;
        }

        return basePrice;
    }

    public List<Ticket> getUserTickets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return ticketRepository.findByUserIdOrderByPurchaseDateDesc(user.getId());
    }
}