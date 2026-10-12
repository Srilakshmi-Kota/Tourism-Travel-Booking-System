package com.tourism.payment_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long bookingId;
    private double amount;
    private String method;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;

    public Payment() {}

    public Payment(Long bookingId, double amount, String method) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = "SUCCESS";
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
