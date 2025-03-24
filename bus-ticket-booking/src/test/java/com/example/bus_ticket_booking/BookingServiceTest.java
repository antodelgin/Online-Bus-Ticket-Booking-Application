package com.example.bus_ticket_booking;

import com.example.bus_ticket_booking.entity.Booking;
import com.example.bus_ticket_booking.entity.Bus;
import com.example.bus_ticket_booking.entity.User;
import com.example.bus_ticket_booking.exception.InsufficientSeatsException;
import com.example.bus_ticket_booking.exception.ResourceNotFoundException;
import com.example.bus_ticket_booking.repository.BookingRepository;
import com.example.bus_ticket_booking.repository.BusRepository;
import com.example.bus_ticket_booking.repository.UserRepository;
import com.example.bus_ticket_booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Bus bus;
    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bus = new Bus();
        bus.setId(1L);
        bus.setBusName("Express Bus");
        bus.setBusNumber("BUS123");
        bus.setSource("City A");
        bus.setDestination("City B");
        bus.setDepartureTime(LocalDateTime.now().plusDays(1));
        bus.setDuration(2l);
        bus.setTotalSeats(40l);
        bus.setAvailableSeats(10l);
        bus.setPrice(BigDecimal.valueOf(500.0));

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");

        booking = new Booking();
        booking.setId(1L);
        booking.setBus(bus);
        booking.setUser(user);
        booking.setName("Test User");
        booking.setEmail("test@example.com");
        booking.setPhoneNumber("1234567890");
        booking.setAge(30);
        booking.setGender("Male");
        booking.setBookingTime(LocalDateTime.now());
    }

    @Test
    void saveBooking_Success() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking savedBooking = bookingService.saveBooking(booking);

        assertNotNull(savedBooking);
        assertEquals(1L, savedBooking.getId());
        assertEquals(9, bus.getAvailableSeats());
        verify(busRepository, times(1)).save(bus);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void saveBooking_NoAvailableSeats() {
        bus.setAvailableSeats(0l);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        assertThrows(InsufficientSeatsException.class, () -> {
            bookingService.saveBooking(booking);
        });
        verify(busRepository, never()).save(any(Bus.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBooking_BusNotFound() {
        when(busRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.saveBooking(booking);
        });
        verify(busRepository, never()).save(any(Bus.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.getBooking(1L);

        assertNotNull(foundBooking);
        assertEquals(1L, foundBooking.getId());
        assertEquals("Test User", foundBooking.getName());
    }

    @Test
    void getBooking_NotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.getBooking(999L);
        });
    }

    @Test
    void getUserBookings_Success() {
        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findByEmail("test@example.com")).thenReturn(bookings);

        List<Booking> userBookings = bookingService.getUserBookings("test@example.com");

        assertNotNull(userBookings);
        assertEquals(1, userBookings.size());
        assertEquals(1L, userBookings.get(0).getId());
    }

    @Test
    void getUserBookings_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.getUserBookings("nonexistent@example.com");
        });
    }

    @Test
    void getUserBookingHistory_Success() {
        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByUser(user)).thenReturn(bookings);

        List<Booking> userBookingHistory = bookingService.getUserBookingHistory(1L);

        assertNotNull(userBookingHistory);
        assertEquals(1, userBookingHistory.size());
        assertEquals(1L, userBookingHistory.get(0).getId());
    }

    @Test
    void getUserBookingHistory_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.getUserBookingHistory(999L);
        });
    }
}
