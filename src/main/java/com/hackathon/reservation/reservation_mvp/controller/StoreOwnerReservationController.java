package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.*;

/**
 * Handles store-owner operations on reservations: list, accept, deny, cancel, history.
 */
@RestController
@RequestMapping("/v1/stores/{storeId}/reservations")
@RequiredArgsConstructor
public class StoreOwnerReservationController {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;

    @GetMapping
    @Operation(summary = "매장 점주 예약 목록 조회 API",
            description = "매장에 접수된 예약 목록과 상태를 조회합니다.")
    public ApiResponse<ReservationResponseDto.ReservationListDto> getReservations(
            @PathVariable Long storeId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {

        Page<Reservation> pageResult = reservationQueryService.getReservations(storeId, page);
        // <-- now calls toListDto(...)
        return ApiResponse.ofSuccess(ReservationConverter.toListDto(pageResult));
    }

    @PatchMapping("/{reservationId}:accept")
    @Operation(summary = "가게 예약 허용 API",
            description = "예약 상태를 AVAILABLE로 변경합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchReservationAccept(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        Reservation saved = reservationCommandService
                .patchReservationStatus(storeId, reservationId, AVAILABLE);
        // <-- now calls toStateDto(...)
        return ApiResponse.ofSuccess(ReservationConverter.toStateDto(saved));
    }

    @PatchMapping("/{reservationId}:deny")
    @Operation(summary = "가게 예약 거절 API",
            description = "예약 상태를 DENIED로 변경합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchReservationDeny(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        Reservation saved = reservationCommandService
                .patchReservationStatus(storeId, reservationId, DENIED);
        return ApiResponse.ofSuccess(ReservationConverter.toStateDto(saved));
    }

    @PatchMapping("/{reservationId}:cancel")
    @Operation(summary = "가게 예약 취소 API",
            description = "예약 상태를 CANCELED로 변경합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> patchReservationCancelByStore(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        Reservation saved = reservationCommandService
                .patchReservationStatus(storeId, reservationId, CANCELED);
        // <-- now calls toCancelDto(...)
        return ApiResponse.ofSuccess(ReservationConverter.toCancelDto(saved, "STORE"));
    }

    @GetMapping("/history")
    @Operation(summary = "매장 예약 내역 조회 API",
            description = "현재까지의 CONFIRMED 예약 내역을 조회합니다.")
    public ApiResponse<ReservationResponseDto.ReservationListDto> getReservationHistory(
            @PathVariable Long storeId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {

        Page<Reservation> pageResult = reservationQueryService.getReservationCalendar(storeId, page);
        return ApiResponse.ofSuccess(ReservationConverter.toListDto(pageResult));
    }
}