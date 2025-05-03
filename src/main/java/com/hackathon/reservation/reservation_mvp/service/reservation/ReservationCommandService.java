package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;

/**
 * Defines commands to change the status of a {@link Reservation}.
 */
public interface ReservationCommandService {

    /**
     * Patches the status of a reservation for a given store.
     *
     * @param storeId       the identifier of the store that owns the reservation
     * @param reservationId the identifier of the reservation to update
     * @param status        the new {@link ReservationStatus} to apply
     * @return the updated {@link Reservation}
     */
    Reservation patchReservationStatus(
            Long storeId, Long reservationId, ReservationStatus status);

    /**
     * Patches the status of a reservation for a given member.
     *
     * @param memberId      the identifier of the member who owns the reservation
     * @param reservationId the identifier of the reservation to update
     * @param status        the new {@link ReservationStatus} to apply
     * @return the updated {@link Reservation}
     */
    Reservation patchReservationStatusByMember(
            Long memberId, Long reservationId, ReservationStatus status);
}