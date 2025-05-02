package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import org.springframework.data.domain.Page;

public interface ReservationQueryService {
    Page<Reservation> getReservations (Long storeId, Integer page);
    Page<Reservation> getReservationCalendar (Long storeId, Integer page);


}
