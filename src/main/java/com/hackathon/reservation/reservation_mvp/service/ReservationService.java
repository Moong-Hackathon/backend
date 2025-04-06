package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.controller.ReservationEventController;
import com.hackathon.reservation.reservation_mvp.dto.ReservationEvent;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationEventController eventController;

    public ReservationService(ReservationRepository reservationRepository, ReservationEventController eventController) {
        this.reservationRepository = reservationRepository;
        this.eventController = eventController;
    }

    /**
     * 예약 상태를 업데이트하고, 변경 이벤트를 전송합니다.
     * @param reservationId 업데이트할 예약의 ID
     * @param newStatus 업데이트할 상태 (예: "AVAILABLE", "CONFIRMED" 등)
     */
    public void updateReservationStatus(Long reservationId, String newStatus) {
        // 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // 예약 상태 업데이트
        reservation.setStatus(
                Enum.valueOf(
                        com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.class, newStatus)
        );
        reservationRepository.save(reservation);

        // 이벤트 생성 (업데이트 시각 포함)
        ReservationEvent event = new ReservationEvent(
                reservation.getReservationId(),
                reservation.getStore().getStoreId(),
                newStatus,
                LocalDateTime.now()
        );

        // 가정: 예약 엔티티 내에 member 필드가 존재하여 사용자 ID를 구할 수 있음
        Long userId = reservation.getMember().getMemberId(); // 만약 User라면 getUserId() 사용

        // SSE 이벤트 전송
        eventController.sendEvent(userId, event);
    }
}