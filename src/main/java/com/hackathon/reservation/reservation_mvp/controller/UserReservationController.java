package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.CANCELED;
import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.CONFIRMED;

/**
 * Handles user-side actions on existing reservations: confirm and cancel.
 */
@RestController
@RequestMapping("/v1/users/{userId}/reservations")
@RequiredArgsConstructor
public class UserReservationController {

    private final ReservationCommandService reservationCommandService;

    @PatchMapping("/{reservationId}:confirm")
    @Operation(summary = "사용자 예약 확정 API",
            description = "AVAILABLE 상태의 예약을 CONFIRMED 상태로 전환합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchReservationConfirmed(
            @PathVariable("reservationId") Long reservationId) {

        Reservation updated = reservationCommandService
                .updateReservationStatus(reservationId, CONFIRMED);

        return ApiResponse.ofSuccess(
                ReservationConverter.toStateDto(updated));
    }

    @PatchMapping("/{reservationId}:cancel")
    @Operation(summary = "사용자 예약 취소 API",
            description = "예약을 CANCELED 상태로 전환하고 취소 주체를 MEMBER로 기록합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> patchReservationCancelByMember(
            @PathVariable("reservationId") Long reservationId) {

        Reservation updated = reservationCommandService
                .updateReservationStatus(reservationId, CANCELED);

        return ApiResponse.ofSuccess(
                ReservationConverter.toCancelDto(updated, "MEMBER"));
    }
}