package com.hackathon.reservation.reservation_mvp.entity;

import com.hackathon.reservation.reservation_mvp.entity.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Defines opening and closing times for a {@link Store} on a specific day.
 */
@Entity
@Table(name = "store_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreSchedule {

    /** Primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Day of the week this schedule applies to. */
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    /** Opening time (inclusive). */
    private LocalTime openTime;

    /** Closing time (inclusive). */
    private LocalTime closeTime;

    /** The store that this schedule belongs to. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}