package com.example.bus_ticket_booking;

import com.example.bus_ticket_booking.entity.Booking;
import com.example.bus_ticket_booking.entity.Bus;
import com.example.bus_ticket_booking.entity.User;
import com.example.bus_ticket_booking.service.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PdfServiceTest {

    @InjectMocks
    private PdfService pdfService;

    private Booking booking;
    private Bus bus;
    private User user;

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
    void generateETicketPdf_Success() {
        byte[] pdfBytes = pdfService.generateETicketPdf(booking);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        String pdfHeader = new String(pdfBytes, 0, 5);
        assertEquals("%PDF-", pdfHeader);
    }

    @Test
    void generateETicketPdf_NullBooking() {
        assertThrows(RuntimeException.class, () -> {
            pdfService.generateETicketPdf(null);
        });
    }
}
