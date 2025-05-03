package com.hackathon.reservation.reservation_mvp.port.out;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;

public interface SaveReservationPort {
    Reservation save(Reservation reservation);
}