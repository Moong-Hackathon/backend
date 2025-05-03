package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import org.springframework.data.domain.Page;

/**
 * Defines queries to fetch {@link Reservation} pages for a store.
 */
public interface ReservationQueryService {

    /**
     * Retrieves a page of all reservations for a store.
     *
     * @param storeId the store identifier
     * @param page    zero-based page index
     * @return a {@link Page} of reservations
     */
    Page<Reservation> getReservations(Long storeId, Integer page);

    /**
     * Retrieves a page of confirmed reservations (history) for a store.
     *
     * @param storeId the store identifier
     * @param page    zero-based page index
     * @return a {@link Page} of confirmed reservations
     */
    Page<Reservation> getReservationCalendar(Long storeId, Integer page);
}