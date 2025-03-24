package com.example.bus_ticket_booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age cannot be more than 100")
    @Column(nullable = false)
    private int age;

    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    private String seatingPreference;

}
