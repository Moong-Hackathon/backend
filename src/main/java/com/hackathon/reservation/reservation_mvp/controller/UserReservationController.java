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

@RestController
@RequestMapping("/v1/users/{userId}/reservations")
@RequiredArgsConstructor
public class UserReservationController {

    private final ReservationCommandService reservationCommandService;

    @PatchMapping("/{reservationId}:confirm")
    @Operation(summary = "사용자 예약 확정 API", description = "점주가 예약 가능으로 승인한 예약(AVAILABLE)을 사용자가 최종 확정(CONFIRMED)합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchReservationConfirmed(
            @PathVariable("userId") Long memberId,
            @PathVariable("reservationId") Long reservationId) {
        Reservation reservation = reservationCommandService.patchReservationStatusByMember(memberId, reservationId, CONFIRMED);
        return ApiResponse.onSuccess(ReservationConverter.reservationStateDto(reservation));
    }

    @PatchMapping("/{reservationId}:cancel")
    @Operation(summary = "사용자 예약 취소 전환 API", description = "사용자가 이미 생성한 예약을 취소할 수 있으며, 예약 취소 시 취소 주체를 MEMBER로 기록합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> patchReservationCancelByMember(
            @PathVariable("userId") Long memberId,
            @PathVariable("reservationId") Long reservationId) {
        Reservation reservation = reservationCommandService.patchReservationStatusByMember(memberId, reservationId, CANCELED);
        return ApiResponse.onSuccess(ReservationConverter.reservationStateCancelDto(reservation, "MEMBER"));
    }
}
