package com.ads.model;

public enum PropertyType {
    HOUSE("House"),
    APARTMENT("Apartment"),
    CONDO("Condo"),
    TOWNHOUSE("Townhouse"),
    VILLA("Villa"),
    STUDIO("Studio"),
    DUPLEX("Duplex"),
    PENTHOUSE("Penthouse"),
    LAND("Land"),
    COMMERCIAL("Commercial"),
    OFFICE("Office"),
    WAREHOUSE("Warehouse");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}