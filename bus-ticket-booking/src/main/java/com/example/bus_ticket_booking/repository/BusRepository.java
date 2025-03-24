package com.example.bus_ticket_booking.repository;

import com.example.bus_ticket_booking.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BusRepository extends JpaRepository<Bus,Long> {
//    List<Bus> findBySourceAndDestinationAndDepartureTime(String source, String destination, LocalDateTime travelDate);
//    List<Bus> findBySourceAndDestination(String source, String destination);
//    boolean existsByBusNumber(String busNumber);
    List<Bus> findBySourceAndDestinationAndDepartureTimeBetween(String source, String destination,
                                                                LocalDateTime startOfDay, LocalDateTime endOfDay);

}
