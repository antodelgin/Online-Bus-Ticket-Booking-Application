package com.example.bus_ticket_booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bus")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bus number cannot be blank")
    @Column(nullable = false,unique = true)
    private String busNumber;

    @NotBlank(message = "Bus name cannot be blank")
    @Column(nullable = false)
    private String busName;

    @NotBlank(message = "Source cannot be blank")
    @Column(nullable = false)
    private String source;

    @NotBlank(message = "Destination cannot be blank")
    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Min(value = 1, message = "Duration must be at least 4 hour")
    @Column(nullable = false)
    private Long duration;

    @Min(value = 1, message = "Total seats must be at least 1")
    @Column(nullable = false)
    private Long totalSeats;

    @Min(value = 0, message = "Available seats cannot be negative")
    private Long availableSeats;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false)
    private BigDecimal price;

}
