package com.example.bus_ticket_booking.controller;

import com.example.bus_ticket_booking.Dto.PasswordChangeRequest;
import com.example.bus_ticket_booking.Dto.UserDto;
import com.example.bus_ticket_booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user management")
public class UserController {

    private UserService userService;

    @Operation(summary = "View user profile", description = "Displays the user profile based on user ID")
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.viewUser(id));
        return "user-profile";
    }

    @Operation(summary = "Show update form", description = "Displays user update form")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.viewUser(id));
        return "update-user";
    }

    @Operation(summary = "Update user details", description = "Updates user information")
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute UserDto userDto) {
        userService.updateUser(id, userDto);
        return "redirect:/users/" + id;
    }

    @Operation(summary = "Show password change form", description = "Displays the password change form")
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        return "change-password";
    }


    @Operation(summary = "Change user password", description = "Allows users to change their passwords securely")
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute PasswordChangeRequest passwordChangeRequest, Model model) {
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            model.addAttribute("error", "New password and confirm password do not match.");
            return "change-password";
        }

        try {
            userService.changePassword(passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword());
            model.addAttribute("success", "Password changed successfully.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "change-password";
    }
}

