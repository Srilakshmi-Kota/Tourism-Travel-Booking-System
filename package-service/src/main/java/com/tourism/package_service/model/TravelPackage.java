package com.tourism.package_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TravelPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination;
    private String title;
    private String description;
    private int days;
    private int nights;
    private double price;
    private String hotel;
    private boolean transportIncluded;
    private boolean sightseeingIncluded;

    protected TravelPackage() {}

    private TravelPackage(Builder b) {
        this.destination = b.destination;
        this.title = b.title;
        this.description = b.description;
        this.days = b.days;
        this.nights = b.nights;
        this.price = b.price;
        this.hotel = b.hotel;
        this.transportIncluded = b.transportIncluded;
        this.sightseeingIncluded = b.sightseeingIncluded;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String destination;
        private String title;
        private String description;
        private int days;
        private int nights;
        private double price;
        private String hotel;
        private boolean transportIncluded;
        private boolean sightseeingIncluded;

        public Builder destination(String v) { destination = v; return this; }
        public Builder title(String v) { title = v; return this; }
        public Builder description(String v) { description = v; return this; }
        public Builder days(int v) { days = v; return this; }
        public Builder nights(int v) { nights = v; return this; }
        public Builder price(double v) { price = v; return this; }
        public Builder hotel(String v) { hotel = v; return this; }
        public Builder transportIncluded(boolean v) { transportIncluded = v; return this; }
        public Builder sightseeingIncluded(boolean v) { sightseeingIncluded = v; return this; }
        public TravelPackage build() { return new TravelPackage(this); }
    }

    public Long getId() { return id; }
    public String getDestination() { return destination; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDays() { return days; }
    public int getNights() { return nights; }
    public double getPrice() { return price; }
    public String getHotel() { return hotel; }
    public boolean isTransportIncluded() { return transportIncluded; }
    public boolean isSightseeingIncluded() { return sightseeingIncluded; }

    public void setDestination(String v) { destination = v; }
    public void setTitle(String v) { title = v; }
    public void setDescription(String v) { description = v; }
    public void setDays(int v) { days = v; }
    public void setNights(int v) { nights = v; }
    public void setPrice(double v) { price = v; }
    public void setHotel(String v) { hotel = v; }
    public void setTransportIncluded(boolean v) { transportIncluded = v; }
    public void setSightseeingIncluded(boolean v) { sightseeingIncluded = v; }
}
