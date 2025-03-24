package com.example.bus_ticket_booking.controller;

import com.example.bus_ticket_booking.Dto.BookingDto;
import com.example.bus_ticket_booking.entity.Booking;
import com.example.bus_ticket_booking.entity.Bus;
import com.example.bus_ticket_booking.service.BookingService;
import com.example.bus_ticket_booking.service.BusService;
import com.example.bus_ticket_booking.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/bookings")
@Tag(name = "Booking Management", description = "Endpoints for booking bus tickets")
public class BookingController {
    private BookingService bookingService;
    private BusService busService;
    private PdfService pdfService;

    @Operation(summary = "Show booking form", description = "Displays the booking form for a specific bus")
    @GetMapping("/new/{busId}")
    public String showBookingForm(@PathVariable Long busId, Model model) {
        Bus bus = busService.getBusById(busId);
        model.addAttribute("bus", bus);
        model.addAttribute("bookingDto", new BookingDto());
        return "booking-form";
    }

    @Operation(summary = "Save booking", description = "Processes and saves a new bus ticket booking")
    @PostMapping("/save/{busId}")
    public String saveBooking(@PathVariable Long busId, @Valid @ModelAttribute("bookingDto") BookingDto bookingDto, BindingResult result, Model model) {

        Bus bus = busService.getBusById(busId);
        if (bus == null) {
            model.addAttribute("error", "Bus not found!");
            return "booking-form";
        }


        if (result.hasErrors()) {
            model.addAttribute("bus",bus);
            return "booking-form";
        }

        if (bus.getAvailableSeats() <= 0) {
            model.addAttribute("error", "No seats available for this bus.");
            model.addAttribute("bus", bus);
            return "booking-form";
        }

        Booking booking = new Booking();
        booking.setBus(busService.getBusById(busId));
        booking.setName(bookingDto.getName());
        booking.setEmail(bookingDto.getEmail());
        booking.setPhoneNumber(bookingDto.getPhoneNumber());
        booking.setAge(bookingDto.getAge());
        booking.setGender(bookingDto.getGender());
        booking.setBookingTime(LocalDateTime.now());

        bus.setAvailableSeats(bus.getAvailableSeats() - 1);
        busService.updateBus(bus,busId);

        Booking savedBooking = bookingService.saveBooking(booking);
        model.addAttribute("booking", savedBooking);
        return "booking-confirmation";
    }

    @Operation(summary = "Get user booking history", description = "Retrieves all bookings for a specific user")
    @GetMapping("/user/{email}")
    public String getUserBookingHistory(@PathVariable String email, Model model) {
        List<Booking> bookings = bookingService.getUserBookings(email);
        model.addAttribute("bookings", bookings);
        return "user-bookings";
    }

    @Operation(summary = "Get booking details", description = "Retrieves details of a specific booking")
    @GetMapping("/{id}")
    public String getBooking(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getBooking(id));
        return "booking-details";
    }

    @Operation(summary = "Download E-Ticket", description = "Generates and downloads a PDF E-Ticket for a booking")
    @GetMapping("/download/{bookingId}")
    public void downloadETicket(@PathVariable Long bookingId, HttpServletResponse response) {
        try {
            Booking booking = bookingService.getBooking(bookingId);
            byte[] pdfBytes = pdfService.generateETicketPdf(booking);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=e-ticket.pdf");
            response.setContentLength(pdfBytes.length);

            OutputStream os = response.getOutputStream();
            os.write(pdfBytes);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}

