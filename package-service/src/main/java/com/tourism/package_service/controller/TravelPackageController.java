package com.tourism.package_service.controller;

import com.tourism.package_service.model.TravelPackage;
import com.tourism.package_service.repository.TravelPackageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*")
public class TravelPackageController {
    private final TravelPackageRepository repository;

    public TravelPackageController(TravelPackageRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TravelPackage> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelPackage> one(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public TravelPackage create(@RequestBody TravelPackage request) {
        TravelPackage p = TravelPackage.builder()
                .destination(request.getDestination())
                .title(request.getTitle())
                .description(request.getDescription())
                .days(request.getDays())
                .nights(request.getNights())
                .price(request.getPrice())
                .hotel(request.getHotel())
                .transportIncluded(request.isTransportIncluded())
                .sightseeingIncluded(request.isSightseeingIncluded())
                .build();
        return repository.save(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelPackage> update(@PathVariable Long id,
                                                @RequestBody TravelPackage request) {
        return repository.findById(id).map(p -> {
            p.setDestination(request.getDestination());
            p.setTitle(request.getTitle());
            p.setDescription(request.getDescription());
            p.setDays(request.getDays());
            p.setNights(request.getNights());
            p.setPrice(request.getPrice());
            p.setHotel(request.getHotel());
            p.setTransportIncluded(request.isTransportIncluded());
            p.setSightseeingIncluded(request.isSightseeingIncluded());
            return ResponseEntity.ok(repository.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
