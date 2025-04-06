package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
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

    @Override
    public Page<Reservation> getReservations (Long storeId, Integer page){
        return reservationRepository.findByStore_StoreId(storeId, PageRequest.of(page, 10));
    }
}
