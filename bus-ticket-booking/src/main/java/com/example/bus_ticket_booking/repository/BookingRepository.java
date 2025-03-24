package com.example.bus_ticket_booking.repository;

import com.example.bus_ticket_booking.entity.Booking;
import com.example.bus_ticket_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByEmail(String email);
    List<Booking> findByUser(User user);

}
