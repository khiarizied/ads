package com.ads.model;

public enum PropertyStatus {
    FOR_SALE("For Sale"),
    FOR_RENT("For Rent"),
    SOLD("Sold"),
    RENTED("Rented"),
    PENDING("Pending"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    PropertyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}