package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.apipayload.code.status.ErrorStatus;
import com.hackathon.reservation.reservation_mvp.apipayload.exception.GeneralException;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import com.hackathon.reservation.reservation_mvp.service.ReservationNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ReservationCommandService} that updates reservation states.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final ReservationRepository reservationRepository;
    private final ReservationNotificationService notificationService;

    @Override
    public Reservation patchReservationStatus(
            Long storeId, Long reservationId, ReservationStatus status) {

        Reservation reservation = reservationRepository
                .findByReservationIdAndStore_StoreId(reservationId, storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESERVATION_STORE_MISMATCH));

        switch (status) {
            case AVAILABLE, DENIED -> {
                if (reservation.getStatus() != ReservationStatus.PENDING) {
                    throw new GeneralException(ErrorStatus.RESERVATION_IS_NOT_PENDING);
                }
                if (status == ReservationStatus.AVAILABLE) {
                    reservation.markAvailable();
                } else {
                    reservation.markDenied();
                }
            }
            case CANCELED -> {
                if (reservation.getStatus() == ReservationStatus.DENIED
                        || reservation.getStatus() == ReservationStatus.CANCELED) {
                    throw new GeneralException(ErrorStatus.RESERVATION_CANNOT_CANCEL);
                }
                reservation.cancel("STORE");
            }
            default -> throw new GeneralException(ErrorStatus.INVALID_RESERVATION_STATUS);
        }

        // Notify user of status change
        notificationService.notifyReservationUpdate(
                reservation.getMember().getMemberId(), reservation);

        return reservation;
    }

    @Override
    public Reservation patchReservationStatusByMember(
            Long memberId, Long reservationId, ReservationStatus status) {

        Reservation reservation = reservationRepository
                .findByReservationIdAndMember_MemberId(reservationId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESERVATION_MEMBER_MISMATCH));

        switch (status) {
            case CONFIRMED -> {
                if (reservation.getStatus() != ReservationStatus.AVAILABLE) {
                    throw new GeneralException(ErrorStatus.RESERVATION_IS_NOT_AVAILABLE);
                }
                reservation.markConfirmed();
            }
            case CANCELED -> {
                if (reservation.getStatus() == ReservationStatus.DENIED
                        || reservation.getStatus() == ReservationStatus.CANCELED) {
                    throw new GeneralException(ErrorStatus.RESERVATION_CANNOT_CANCEL);
                }
                reservation.cancel("MEMBER");
            }
            default -> throw new GeneralException(ErrorStatus.INVALID_RESERVATION_STATUS);
        }

        return reservation;
    }
}