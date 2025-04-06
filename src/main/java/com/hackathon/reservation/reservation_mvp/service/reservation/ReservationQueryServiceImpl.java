package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.apipayload.code.status.ErrorStatus;
import com.hackathon.reservation.reservation_mvp.apipayload.exception.GeneralException;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import com.hackathon.reservation.reservation_mvp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryServiceImpl implements ReservationQueryService {
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    @Override
    public Page<Reservation> getReservations (Long storeId, Integer page){
        if (!storeRepository.existsById(storeId)) {
            throw new GeneralException(ErrorStatus.STORE_NOT_FOUND);
        }

        return reservationRepository.findByStore_StoreId(storeId, PageRequest.of(page, 10));
    }
}
