package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.port.in.UpdateReservationStatusUseCase;
import com.hackathon.reservation.reservation_mvp.port.out.LoadReservationPort;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.*;

@RestController
@RequestMapping("/v1/stores/{storeId}/reservations")
@RequiredArgsConstructor
public class StoreOwnerReservationController {

    private final UpdateReservationStatusUseCase updateStatus;
    private final LoadReservationPort loadReservationPort;
    private final ReservationConverter converter;

    @PatchMapping("/{reservationId}:accept")
    @Operation(summary = "가게 예약 허용", description = "예약 상태를 AVAILABLE로 변경합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> accept(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        // 1) 상태 변경 커맨드 전송
        updateStatus.updateStatus(
                new UpdateReservationStatusUseCase.UpdateStatusCommand(
                        reservationId,
                        AVAILABLE.name()
                )
        );

        // 2) 변경된 엔티티 다시 조회해서 DTO 변환
        var reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        return ApiResponse.ofSuccess(converter.toStateDto(reservation));
    }

    @PatchMapping("/{reservationId}:deny")
    @Operation(summary = "가게 예약 거절", description = "예약 상태를 DENIED로 변경합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> deny(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        updateStatus.updateStatus(
                new UpdateReservationStatusUseCase.UpdateStatusCommand(
                        reservationId,
                        DENIED.name()
                )
        );
        var reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        return ApiResponse.ofSuccess(converter.toStateDto(reservation));
    }

    @PatchMapping("/{reservationId}:cancel")
    @Operation(summary = "가게 예약 취소", description = "예약 상태를 CANCELED로 변경합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> cancel(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        updateStatus.updateStatus(
                new UpdateReservationStatusUseCase.UpdateStatusCommand(
                        reservationId,
                        CANCELED.name()
                )
        );
        var reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        return ApiResponse.ofSuccess(converter.toCancelDto(reservation, "STORE"));
    }
}