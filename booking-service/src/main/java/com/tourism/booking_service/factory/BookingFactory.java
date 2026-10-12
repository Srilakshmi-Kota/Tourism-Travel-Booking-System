package com.tourism.booking_service.factory;

import com.tourism.booking_service.model.Booking;

public final class BookingFactory {
    private BookingFactory() {}

    public static Booking create(String type, Long userId, Long packageId,
                                 int travellers, double totalAmount) {
        String normalized = type == null ? "TOUR" : type.toUpperCase();

        return switch (normalized) {
            case "TOUR", "HOTEL", "FLIGHT" ->
                    new Booking(userId, packageId, normalized, travellers, totalAmount);
            default -> throw new IllegalArgumentException(
                    "Unsupported booking type: " + type + ". Use TOUR, HOTEL or FLIGHT.");
        };
    }
}
