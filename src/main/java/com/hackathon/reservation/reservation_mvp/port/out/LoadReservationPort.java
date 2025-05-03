package com.hackathon.reservation.reservation_mvp.port.out;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;

import java.util.Optional;

public interface LoadReservationPort {
    Optional<Reservation> findById(Long reservationId);
}