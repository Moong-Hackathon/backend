package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;

/**
 * 명령 전용 서비스 (Command).
 * 예약 생성·상태 변경 같은 쓰기 작업만 담당합니다.
 */
public interface ReservationCommandService {

    /**
     * 사용자의 예약 요청을 처리하고, 예약이 가능한 매장 수를 반환합니다.
     *
     * @param userId 사용자 ID
     * @param dto    예약 요청 DTO
     * @return 예약이 생성된 매장 수
     */
    int reserveAllAvailableStores(Long userId, StoreReservationRequestDto dto);

    /**
     * 예약 상태를 업데이트합니다.
     *
     * @param reservationId 예약 ID
     * @param newStatus     새 상태
     * @return 업데이트된 Reservation 엔티티
     */
    Reservation updateReservationStatus(Long reservationId, ReservationStatus newStatus);
}