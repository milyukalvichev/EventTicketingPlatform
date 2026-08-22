package com.ticketing.main_app.repository;

import com.ticketing.main_app.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByEventDateAsc();

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.venue.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.venue.city) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY e.eventDate ASC")
    List<Event> searchEvents(@Param("query") String query);
}