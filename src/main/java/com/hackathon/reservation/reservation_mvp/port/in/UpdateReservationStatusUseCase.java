package com.hackathon.reservation.reservation_mvp.port.in;

public interface UpdateReservationStatusUseCase {
    void updateStatus(UpdateStatusCommand cmd);

    record UpdateStatusCommand(
            Long reservationId,
            String newStatus
    ) {}
}