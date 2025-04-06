package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;

public interface ReservationCommandService {
    Reservation patchReservationStatus (Long storeId, Long reservationId, Enum<ReservationStatus> status);
    Reservation patchReservationStatusByMember(Long memberId, Long reservationId, Enum<ReservationStatus> status);
}
