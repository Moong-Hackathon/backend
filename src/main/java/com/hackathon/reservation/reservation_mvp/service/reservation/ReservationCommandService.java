package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;

/**
 * Command‐side operations for reservations.
 */
public interface ReservationCommandService {
    /** 사용자 예약 요청 (여러 매장에 동시에 PENDING 예약 생성) */
    int reserveAllAvailableStores(Long userId, StoreReservationRequestDto dto);

    /** 단일 예약 상태 전이 (ACCEPT, DENY, CONFIRM, CANCEL) */
    Reservation updateReservationStatus(Long reservationId, ReservationStatus newStatus);
}