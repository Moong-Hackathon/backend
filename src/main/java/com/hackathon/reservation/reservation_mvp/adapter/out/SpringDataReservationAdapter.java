package com.hackathon.reservation.reservation_mvp.adapter.out;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.port.out.LoadReservationPort;
import com.hackathon.reservation.reservation_mvp.port.out.SaveReservationPort;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpringDataReservationAdapter implements
        LoadReservationPort,
        SaveReservationPort {

    private final ReservationRepository reservationRepository;

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}