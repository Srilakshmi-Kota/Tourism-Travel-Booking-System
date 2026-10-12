package com.tourism.booking_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long packageId;
    private String bookingType;
    private int travellers;
    private double totalAmount;
    private String status;
    private LocalDateTime bookingDate;

    public Booking() {}

    public Booking(Long userId, Long packageId, String bookingType, int travellers, double totalAmount) {
        this.userId = userId;
        this.packageId = packageId;
        this.bookingType = bookingType;
        this.travellers = travellers;
        this.totalAmount = totalAmount;
        this.status = "CONFIRMED";
        this.bookingDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getPackageId() { return packageId; }
    public String getBookingType() { return bookingType; }
    public int getTravellers() { return travellers; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public LocalDateTime getBookingDate() { return bookingDate; }

    public void cancel() { this.status = "CANCELLED"; }
}
