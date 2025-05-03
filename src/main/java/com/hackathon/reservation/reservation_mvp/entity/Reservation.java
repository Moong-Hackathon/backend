package com.hackathon.reservation.reservation_mvp.entity;

import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a booking request by a {@link Member} at a {@link Store}.
 */
@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reservation {

    /** Primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    /** The member who made this reservation. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** The store being reserved. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    /** The desired date and time. */
    private LocalDateTime reservationTime;

    /** Number of people included in the party. */
    private Integer numberOfPeople;

    /** Current status in the reservation workflow. */
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    /** Who cancelled (“STORE” or “MEMBER”), if applicable. */
    private String canceledBy;

    /** When this record was created. */
    private LocalDateTime createdAt;

    /** When this record was last updated. */
    private LocalDateTime updatedAt;

    //===== Business methods for transitioning status =====

    /** Marks this reservation as AVAILABLE. */
    public void markAvailable() {
        this.status = ReservationStatus.AVAILABLE;
    }

    /** Marks this reservation as CONFIRMED. */
    public void markConfirmed() {
        this.status = ReservationStatus.CONFIRMED;
    }

    /** Marks this reservation as DENIED. */
    public void markDenied() {
        this.status = ReservationStatus.DENIED;
    }

    /**
     * Cancels this reservation.
     *
     * @param by “STORE” or “MEMBER”
     */
    public void cancel(String by) {
        this.status = ReservationStatus.CANCELED;
        this.canceledBy = by;
    }

    /**
     * Updates the last‐updated timestamp.
     *
     * @param updatedAt new updated timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}