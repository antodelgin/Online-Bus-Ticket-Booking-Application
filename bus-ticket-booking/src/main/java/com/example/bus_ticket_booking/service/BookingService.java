package com.example.bus_ticket_booking.service;

import com.example.bus_ticket_booking.entity.Booking;
import com.example.bus_ticket_booking.entity.Bus;
import com.example.bus_ticket_booking.entity.User;
import com.example.bus_ticket_booking.exception.InsufficientSeatsException;
import com.example.bus_ticket_booking.exception.ResourceNotFoundException;
import com.example.bus_ticket_booking.repository.BookingRepository;
import com.example.bus_ticket_booking.repository.BusRepository;
import com.example.bus_ticket_booking.repository.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@AllArgsConstructor
public class BookingService {

    private BookingRepository bookingRepository;

    private BusRepository busRepository;
    private UserRepository userRepository;

    public Booking saveBooking(Booking booking) {
        Bus bus = busRepository.findById(booking.getBus().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: "+ booking.getBus().getId()));

        if (bus.getAvailableSeats() <= 0) {
            throw new InsufficientSeatsException("No seats available for this bus.");
        }

        bus.setAvailableSeats(bus.getAvailableSeats() - 1);
        busRepository.save(bus);

        booking.setBookingTime(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: "+ id));
    }

    public List<Booking> getUserBookings(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Email: "+ email ));
        return bookingRepository.findByEmail(email);

    }

    public List<Booking> getUserBookingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: "+ userId));
        return bookingRepository.findByUser(user);
    }

}

