package com.storeapp.activity.entity;

/**
 * Transport mode for Trip activities (origin→destination)
 * Maps to transport_mode_enum in PostgreSQL
 */
public enum TransportMode {
    FLIGHT,
    TRAIN,
    BUS,
    CAR,
    FERRY,
    BIKE,
    WALK,
    OTHER
}
