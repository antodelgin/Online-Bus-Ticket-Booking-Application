package com.example.bus_ticket_booking.controller;

import com.example.bus_ticket_booking.entity.User;
import com.example.bus_ticket_booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@AllArgsConstructor
public class GlobalController {

    private UserService userService;

    @ModelAttribute
    public void addUserToModel(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.getUserByEmail(principal.getName());
            model.addAttribute("user", user);
        }
    }
}
