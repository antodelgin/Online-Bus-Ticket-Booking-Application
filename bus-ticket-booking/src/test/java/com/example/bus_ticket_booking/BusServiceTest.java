package com.example.bus_ticket_booking;

import com.example.bus_ticket_booking.entity.Bus;
import com.example.bus_ticket_booking.exception.ResourceNotFoundException;
import com.example.bus_ticket_booking.repository.BusRepository;
import com.example.bus_ticket_booking.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusService busService;

    private Bus bus;

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
    }

    @Test
    void addBus_Success() {
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        Bus savedBus = busService.addBus(bus);

        assertNotNull(savedBus);
        assertEquals("Express Bus", savedBus.getBusName());
        assertEquals("BUS123", savedBus.getBusNumber());
        verify(busRepository, times(1)).save(bus);
    }

    @Test
    void getBusById_Success() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        Bus foundBus = busService.getBusById(1L);

        assertNotNull(foundBus);
        assertEquals(1L, foundBus.getId());
        assertEquals("Express Bus", foundBus.getBusName());
    }

    @Test
    void getBusById_NotFound() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            busService.getBusById(999L);
        });
    }

    @Test
    void updateBus_Success() {
        Bus updatedBus = new Bus();
        updatedBus.setBusName("Updated Express");
        updatedBus.setBusNumber("BUS456");
        updatedBus.setSource("City C");
        updatedBus.setDestination("City D");
        updatedBus.setDepartureTime(LocalDateTime.now().plusDays(2));
        updatedBus.setDuration(3l);
        updatedBus.setTotalSeats(45l);
        updatedBus.setAvailableSeats(15l);
        updatedBus.setPrice(BigDecimal.valueOf(600.0));

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        Bus result = busService.updateBus(updatedBus, 1L);

        assertNotNull(result);
        assertEquals("Updated Express", bus.getBusName());
        assertEquals("BUS456", bus.getBusNumber());
        assertEquals("City C", bus.getSource());
        assertEquals("City D", bus.getDestination());
        assertEquals(3.0, bus.getDuration().doubleValue(), 0.01);
        assertEquals(45, bus.getTotalSeats());
        assertEquals(15, bus.getAvailableSeats());
        assertEquals(new BigDecimal("600.0"), bus.getPrice());
        verify(busRepository, times(1)).save(bus);
    }

    @Test
    void updateBus_NotFound() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            busService.updateBus(bus, 999L);
        });
        verify(busRepository, never()).save(any(Bus.class));
    }

    @Test
    void deleteBus_Success() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        doNothing().when(busRepository).deleteById(1L);

        busService.deleteBus(1L);

        verify(busRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBus_NotFound() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            busService.deleteBus(999L);
        });
        verify(busRepository, never()).deleteById(any());
    }

    @Test
    void getAllBuses_Success() {
        List<Bus> buses = Arrays.asList(bus);
        when(busRepository.findAll()).thenReturn(buses);

        List<Bus> allBuses = busService.getAllBuses();

        assertNotNull(allBuses);
        assertEquals(1, allBuses.size());
        assertEquals("Express Bus", allBuses.get(0).getBusName());
    }

    @Test
    void searchBuses_Success() {
        String date = "2025-03-24";
        LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");

        List<Bus> buses = Arrays.asList(bus);
        when(busRepository.findBySourceAndDestinationAndDepartureTimeBetween(
                "City A", "City B", startOfDay, endOfDay)).thenReturn(buses);

        List<Bus> searchedBuses = busService.searchBuses("City A", "City B", date);

        assertNotNull(searchedBuses);
        assertEquals(1, searchedBuses.size());
        assertEquals("Express Bus", searchedBuses.get(0).getBusName());
    }
}