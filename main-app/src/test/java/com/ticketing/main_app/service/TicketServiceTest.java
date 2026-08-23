package com.ticketing.main_app.service;

import com.ticketing.main_app.model.Event;
import com.ticketing.main_app.model.Ticket;
import com.ticketing.main_app.model.User;
import com.ticketing.main_app.model.UserRoleEnum;
import com.ticketing.main_app.model.Venue;
import com.ticketing.main_app.repository.EventRepository;
import com.ticketing.main_app.repository.TicketRepository;
import com.ticketing.main_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestClient pricingRestClient;

    @InjectMocks
    private TicketService ticketService;

    private User testUser;
    private Venue testVenue;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        testUser = new User("test_user", "password123", "test@test.com", UserRoleEnum.USER);
        testVenue = new Venue("Hall 1", "Sofia", 100);
        testEvent = new Event("Symphony", new BigDecimal("50.00"), LocalDateTime.now().plusDays(5), testVenue);
        testEvent.setTickets(new ArrayList<>());
    }

    @Test
    void purchaseTickets_ValidRequest_Success() {
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        List<Ticket> result = ticketService.purchaseTickets("test_user", 1L, null, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(0).getFinalPrice());
        verify(ticketRepository, times(2)).save(any(Ticket.class));
    }

    @Test
    void purchaseTickets_ExceedsCapacity_ThrowsException() {
        testVenue.setCapacity(2);
        testEvent.getTickets().add(new Ticket());
        testEvent.getTickets().add(new Ticket()); // Capacity full

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.purchaseTickets("test_user", 1L, null, 1)
        );
    }

    @Test
    void returnTicket_AuthorizedUser_Success() {
        Ticket ticket = new Ticket(new BigDecimal("50.00"), null, LocalDateTime.now(), testUser, testEvent);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        ticketService.returnTicket(10L, "test_user");

        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    void returnTicket_UnauthorizedUser_ThrowsException() {
        Ticket ticket = new Ticket(new BigDecimal("50.00"), null, LocalDateTime.now(), testUser, testEvent);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.returnTicket(10L, "other_user")
        );
    }
}