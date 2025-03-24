package com.example.bus_ticket_booking.controller;

import com.example.bus_ticket_booking.Dto.LoginRequest;
import com.example.bus_ticket_booking.Dto.UserDto;
import com.example.bus_ticket_booking.security.JwtTokenProvider;
import com.example.bus_ticket_booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@Controller
@AllArgsConstructor
@Tag(name = "Authentication", description = "Handles user authentication and registration")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private JwtTokenProvider tokenProvider;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Show registration page")
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new UserDto());
        return "register-user";
    }

    @Operation(summary = "Register a new user", description = "Registers a user and redirects to login page")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") UserDto registerRequest,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register-user";
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            model.addAttribute("error", "Username is already taken!");
            return "register-user";
        }

        userService.registerUser(registerRequest);

        model.addAttribute("success", "User registered successfully! Please login.");
        return "redirect:/login";
    }

    @Operation(summary = "Show login page")
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @Operation(summary = "Authenticate user and generate JWT token")
    @PostMapping("/login")
    public String authenticateUser(@ModelAttribute("loginRequest") LoginRequest loginRequest,
                                   HttpSession session, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);

            session.setAttribute("jwtToken", jwt);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("user", userDetails);

            return "redirect:/";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}


