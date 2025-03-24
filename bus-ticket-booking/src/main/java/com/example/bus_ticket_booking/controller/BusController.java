package com.example.bus_ticket_booking.controller;

import com.example.bus_ticket_booking.entity.Bus;
import com.example.bus_ticket_booking.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bus Management", description = "Handles bus management operations")
@Controller
@RequestMapping("/buses")
@AllArgsConstructor
public class BusController {
    private BusService busService;

    @Operation(summary = "Get all buses")
    @GetMapping
    public String getAllBuses(Model model) {
        List<Bus> buses = busService.getAllBuses();
        model.addAttribute("buses",buses);
        return "bus-list";
    }

    @Operation(summary = "Get bus by ID")
    @GetMapping("/{id}")
    public String getBusById(@PathVariable Long id, Model model) {
        Bus bus = busService.getBusById(id);
        model.addAttribute("bus", bus);
        return "bus-details";
    }

    @Operation(summary = "Show add bus form")
    @GetMapping("/add")
    public String showAddBusForm(Model model) {
        model.addAttribute("bus", new Bus());
        return "bus-form";
    }

    @Operation(summary = "Add a new bus")
    @PostMapping("/save")
    public String addBus(@ModelAttribute Bus bus) {
        busService.addBus(bus);
        return "redirect:/buses";
    }
    @Operation(summary = "Show update bus form")
    @GetMapping("/update/{id}")
    public String showUpdateBusForm(@PathVariable Long id, Model model) {
        Bus bus = busService.getBusById(id);
        model.addAttribute("bus", bus);
        return "update-bus";
    }

    @Operation(summary = "Update bus details")
    @PostMapping("/update/{id}")
    public String updateBus(@PathVariable Long id, @ModelAttribute Bus bus) {
        busService.updateBus(bus, id);
        return "redirect:/buses";
    }

    @Operation(summary = "Search for buses by source, destination, and date")
    @GetMapping("/search")
    public String searchBuses(@RequestParam String source,
                              @RequestParam String destination,
                              @RequestParam String date,
                              Model model) {
        List<Bus> buses = busService.searchBuses(source, destination, date);
        model.addAttribute("buses", buses);
        return "bus-list";
    }
}
