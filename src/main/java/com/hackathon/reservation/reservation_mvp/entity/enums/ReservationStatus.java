package com.hackathon.reservation.reservation_mvp.entity.enums;

/**
 * The life-cycle states of a reservation.
 */
public enum ReservationStatus {
    /** Initial state when user requests. */
    PENDING,
    /** Store owner approved the request. */
    AVAILABLE,
    /** User confirmed after store approval. */
    CONFIRMED,
    /** Store owner explicitly denied the request. */
    DENIED,
    /** Either party cancelled the reservation. */
    CANCELED
}