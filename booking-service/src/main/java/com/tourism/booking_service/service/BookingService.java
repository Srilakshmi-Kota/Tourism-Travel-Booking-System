package com.tourism.booking_service.service;

import com.tourism.booking_service.factory.BookingFactory;
import com.tourism.booking_service.model.Booking;
import com.tourism.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final RestClient packageClient;

    public BookingService(BookingRepository repository,
                           @Value("${package.service.url}") String packageUrl) {
        this.repository = repository;
        this.packageClient = RestClient.builder().baseUrl(packageUrl).build();
    }

    public Booking create(Long userId, Long packageId, String type, int travellers) {
        if (travellers < 1) throw new IllegalArgumentException("Travellers must be at least 1.");

        PackageDto p = packageClient.get()
                .uri("/api/packages/{id}", packageId)
                .retrieve()
                .body(PackageDto.class);

        if (p == null) throw new IllegalArgumentException("Travel package not found.");

        double total = p.price() * travellers;
        Booking booking = BookingFactory.create(type, userId, packageId, travellers, total);
        return repository.save(booking);
    }

    public List<Booking> all() { return repository.findAll(); }
    public List<Booking> byUser(Long userId) { return repository.findByUserId(userId); }

    public Booking cancel(Long id) {
        Booking b = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        b.cancel();
        return repository.save(b);
    }

    public record PackageDto(Long id, String destination, String title, String description,
                             int days, int nights, double price, String hotel,
                             boolean transportIncluded, boolean sightseeingIncluded) {}
}
