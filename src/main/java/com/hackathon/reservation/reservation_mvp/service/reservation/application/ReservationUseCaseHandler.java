package com.hackathon.reservation.reservation_mvp.service.reservation.application;

import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.port.in.CreateReservationUseCase;
import com.hackathon.reservation.reservation_mvp.port.in.UpdateReservationStatusUseCase;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationUseCaseHandler implements
        CreateReservationUseCase,
        UpdateReservationStatusUseCase {

    private final ReservationCommandService commandService;

    @Override
    public int reserveAll(CreateReservationCommand cmd) {
        // Build the DTO that the domain‐service expects
        StoreReservationRequestDto dto = new StoreReservationRequestDto();
        dto.setUserId(cmd.userId());
        StoreReservationRequestDto.Location loc = new StoreReservationRequestDto.Location();
        loc.setLatitude(cmd.location().getLatitude());
        loc.setLongitude(cmd.location().getLongitude());
        dto.setLocation(loc);
        dto.setDistanceType(cmd.distanceType());
        dto.setNumberOfPeople(cmd.numberOfPeople());
        dto.setReservationTime(cmd.reservationTime());

        return commandService.reserveAllAvailableStores(cmd.userId(), dto);
    }

    @Override
    public void updateStatus(UpdateStatusCommand cmd) {
        // The domain‐service takes a ReservationStatus enum,
        // so we convert the incoming String.
        commandService.updateReservationStatus(
                Long.valueOf(cmd.reservationId()),
                ReservationStatus.valueOf(cmd.newStatus())
        );
    }
}