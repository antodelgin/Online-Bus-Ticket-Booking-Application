package com.example.bus_ticket_booking.controller;

import com.example.bus_ticket_booking.entity.User;
import com.example.bus_ticket_booking.service.BookingService;
import com.example.bus_ticket_booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
@Tag(name = "Home Controller", description = "Handles home page")
public class HomeController {

    private UserService userService;
    private BookingService bookingService;

    @Operation(summary = "Show home page", description = "Displays the home page with user details if logged in")
    @GetMapping("/")
    public String showHomePage(Model model) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
        }
        return "home";
    }
}
