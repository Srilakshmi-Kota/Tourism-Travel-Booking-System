package com.tourism.payment_service.controller;

import com.tourism.payment_service.model.Payment;
import com.tourism.payment_service.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    private final PaymentRepository repository;

    public PaymentController(PaymentRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Payment pay(@RequestBody PaymentRequest request) {
        return repository.save(new Payment(request.bookingId(), request.amount(), request.method()));
    }

    public record PaymentRequest(Long bookingId, double amount, String method) {}
}
