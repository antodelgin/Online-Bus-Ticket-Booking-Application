package com.example.bus_ticket_booking.service;

import com.example.bus_ticket_booking.entity.Bus;
import com.example.bus_ticket_booking.exception.ResourceNotFoundException;
import com.example.bus_ticket_booking.repository.BusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BusService {

    private BusRepository busRepository;

    public Bus addBus(Bus bus)  {

        return busRepository.save(bus);

    }

    public Bus getBusById(Long id) {
        return busRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: "+ id));

    }
    public Bus updateBus(Bus bus, Long id) {
        Bus existingBus = busRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: "+ id));

        existingBus.setBusNumber(bus.getBusNumber());
        existingBus.setBusName(bus.getBusName());
        existingBus.setSource(bus.getSource());
        existingBus.setDestination(bus.getDestination());
        existingBus.setDepartureTime(bus.getDepartureTime());
        existingBus.setDuration(bus.getDuration());
        existingBus.setTotalSeats(bus.getTotalSeats());
        existingBus.setAvailableSeats(bus.getAvailableSeats());
        existingBus.setPrice(bus.getPrice());

        return busRepository.save(existingBus);
    }

    public void deleteBus(Long id){
        busRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: "+ id));
        busRepository.deleteById(id);
    }

    public List<Bus> getAllBuses(){
        return busRepository.findAll();
    }


    public List<Bus> searchBuses(String source, String destination, String date) {
        LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");

        return busRepository.findBySourceAndDestinationAndDepartureTimeBetween(source, destination, startOfDay, endOfDay);
    }
}
