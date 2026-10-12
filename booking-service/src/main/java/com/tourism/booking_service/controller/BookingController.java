package com.tourism.booking_service.controller;

import com.tourism.booking_service.model.Booking;
import com.tourism.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Booking> all() { return service.all(); }

    @GetMapping("/user/{userId}")
    public List<Booking> byUser(@PathVariable Long userId) {
        return service.byUser(userId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateBookingRequest request) {
        try {
            return ResponseEntity.ok(service.create(
                    request.userId(), request.packageId(), request.bookingType(), request.travellers()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", e.getMessage() == null ? "Booking failed" : e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.cancel(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    public record CreateBookingRequest(Long userId, Long packageId, String bookingType, int travellers) {}
}
