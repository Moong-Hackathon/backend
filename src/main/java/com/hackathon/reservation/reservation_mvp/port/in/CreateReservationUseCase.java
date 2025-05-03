package com.hackathon.reservation.reservation_mvp.port.in;

import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;

import java.time.LocalDateTime;

public interface CreateReservationUseCase {
    int reserveAll(CreateReservationCommand cmd);

    record CreateReservationCommand(
            Long userId,
            StoreReservationRequestDto.Location location,
            String distanceType,
            int numberOfPeople,
            LocalDateTime reservationTime
    ) {}
}