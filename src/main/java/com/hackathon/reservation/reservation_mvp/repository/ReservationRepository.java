package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Reservation} entities.
 *
 * <p>Supports queries for reservation lists and lookups by
 * store or member context.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Finds all reservations for the given store, paged.
     *
     * @param storeId the store identifier
     * @param pageable paging information
     * @return a page of reservations
     */
    Page<Reservation> findByStore_StoreId(Long storeId, Pageable pageable);

    /**
     * Looks up a single reservation by its id and store id.
     *
     * @param reservationId the reservation identifier
     * @param storeId the store identifier
     * @return the reservation, if found
     */
    Optional<Reservation> findByReservationIdAndStore_StoreId(
            Long reservationId, Long storeId);

    /**
     * Looks up a single reservation by its id and member id.
     *
     * @param reservationId the reservation identifier
     * @param memberId the member identifier
     * @return the reservation, if found
     */
    Optional<Reservation> findByReservationIdAndMember_MemberId(
            Long reservationId, Long memberId);

    /**
     * Finds all reservations for the given store and status, paged.
     *
     * @param storeId the store identifier
     * @param status the {@link ReservationStatus}
     * @param pageable paging information
     * @return a page of reservations
     */
    Page<Reservation> findByStore_StoreIdAndStatus(
            Long storeId, ReservationStatus status, Pageable pageable);
}