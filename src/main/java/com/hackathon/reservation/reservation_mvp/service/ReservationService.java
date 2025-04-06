package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.dto.ReservationEvent;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationNotificationService notificationService;

    @Transactional
    public void updateReservationStatus(Long reservationId, String newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // 예약 상태 업데이트
        reservation.setStatus(Enum.valueOf(ReservationStatus.class, newStatus));
        reservationRepository.save(reservation);

        // 이벤트 생성 및 전송
        ReservationEvent event = new ReservationEvent(
                reservation.getReservationId(),
                reservation.getStore().getStoreId(),
                newStatus,
                LocalDateTime.now()
        );

        Long userId = reservation.getMember().getMemberId();
        notificationService.notifyReservationUpdate(userId, event);
    }
}