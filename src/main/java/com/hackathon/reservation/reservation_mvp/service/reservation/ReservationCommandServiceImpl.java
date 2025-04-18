package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.apipayload.code.status.ErrorStatus;
import com.hackathon.reservation.reservation_mvp.apipayload.exception.GeneralException;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandServiceImpl implements ReservationCommandService {
    private final ReservationRepository reservationRepository;

    @Override
    public Reservation patchReservationStatus (Long storeId, Long reservationId, Enum<ReservationStatus> status) {
        Reservation reservation = reservationRepository.findByReservationIdAndStore_StoreId(reservationId, storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESERVATION_STORE_MISMATCH));

        switch ((ReservationStatus) status) {
            case AVAILABLE, DENIED -> {
                if (reservation.getStatus() != PENDING)
                    throw new GeneralException(ErrorStatus.RESERVATION_IS_NOT_PENDING);
                if (status == AVAILABLE) reservation.available();
                else reservation.deny();
            }
            case CANCELED -> {
                if (reservation.getStatus() == DENIED || reservation.getStatus() == CANCELED)
                    throw new GeneralException(ErrorStatus.RESERVATION_CANNOT_CANCEL);
                reservation.cancel("STORE");
            }
            default -> throw new GeneralException(ErrorStatus.INVALID_RESERVATION_STATUS);
        }

        return reservation;
    }

    @Override
    public Reservation patchReservationStatusByMember(Long memberId, Long reservationId, Enum<ReservationStatus> status){
        Reservation reservation = reservationRepository.findByReservationIdAndMember_MemberId(reservationId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESERVATION_STORE_MISMATCH));

        switch ((ReservationStatus) status) {
            case CONFIRMED -> {
                if (reservation.getStatus() != AVAILABLE)
                    throw new GeneralException(ErrorStatus.RESERVATION_IS_NOT_AVAILABLE);
                reservation.confirm();
            }

            case CANCELED -> {
                if (reservation.getStatus() == DENIED || reservation.getStatus() == CANCELED)
                    throw new GeneralException(ErrorStatus.RESERVATION_CANNOT_CANCEL);
                reservation.cancel("MEMBER");
            }
            default -> throw new GeneralException(ErrorStatus.INVALID_RESERVATION_STATUS);
        }

        return reservation;


    }
}
