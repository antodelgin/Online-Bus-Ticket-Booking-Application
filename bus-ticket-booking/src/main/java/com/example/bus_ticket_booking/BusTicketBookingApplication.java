package com.example.bus_ticket_booking;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Online Bus Ticket Booking Application API",
				description = "This API provides functionalities for managing bus schedules, ticket bookings, and user profiles. It includes endpoints for searching buses, booking tickets, updating user details,changing user password, view user booking history and downloading e-tickets.",
				version = "1.0",
				contact = @Contact(
						name = "Anto Delgin Anston",
						email = "antodelgin@gmail.com",
						url = "https://www.google.com"
				)
		)
)
public class BusTicketBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusTicketBookingApplication.class, args);
	}


	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

}
