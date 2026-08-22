package com.ticketing.main_app.config;

import com.ticketing.main_app.model.Event;
import com.ticketing.main_app.model.User;
import com.ticketing.main_app.model.UserRoleEnum;
import com.ticketing.main_app.model.Venue;
import com.ticketing.main_app.repository.EventRepository;
import com.ticketing.main_app.repository.UserRepository;
import com.ticketing.main_app.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                          VenueRepository venueRepository,
                                          EventRepository eventRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Seed Users
            if (userRepository.count() == 0) {
                User admin = new User(
                        "admin",
                        passwordEncoder.encode("password123"),
                        "admin@ticketing.com",
                        UserRoleEnum.ADMIN
                );

                User user = new User(
                        "john_doe",
                        passwordEncoder.encode("password123"),
                        "john@example.com",
                        UserRoleEnum.USER
                );

                userRepository.saveAll(List.of(admin, user));
            }

            // 2. Seed Venues & Events
            if (venueRepository.count() == 0) {
                Venue ndk = venueRepository.save(new Venue("National Palace of Culture", "Sofia", 3000));
                Venue ancientTheatre = venueRepository.save(new Venue("Ancient Theatre", "Plovdiv", 2500));

                Event event1 = new Event(
                        "Rock Symphony Festival",
                        new BigDecimal("60.00"),
                        LocalDateTime.now().plusMonths(1),
                        ndk
                );

                Event event2 = new Event(
                        "Tech Innovation Summit 2026",
                        new BigDecimal("120.00"),
                        LocalDateTime.now().plusMonths(2),
                        ancientTheatre
                );

                eventRepository.saveAll(List.of(event1, event2));
            }
        };
    }
}