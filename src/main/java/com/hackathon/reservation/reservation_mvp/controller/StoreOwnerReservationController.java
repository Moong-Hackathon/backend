package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.*;

/**
 * 매장 점주용 예약 관리 API
 */
@RestController
@RequestMapping("/v1/stores/{storeId}/reservations")
@RequiredArgsConstructor
public class StoreOwnerReservationController {

    @Qualifier("reservationQueryServiceImpl")
    private final ReservationQueryService queryService;

    @Qualifier("reservationCommandServiceImpl")
    private final ReservationCommandService commandService;

    @GetMapping
    @Operation(summary = "매장 점주 예약 목록 조회")
    public ApiResponse<ReservationResponseDto.ReservationListDto> getReservations(
            @PathVariable Long storeId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {

        Page<Reservation> p = queryService.getReservations(storeId, page);
        return ApiResponse.ofSuccess(ReservationConverter.toListDto(p));
    }

    @PatchMapping("/{reservationId}:accept")
    @Operation(summary = "가게 예약 허용")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchAccept(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        Reservation r = commandService.updateReservationStatus(reservationId, AVAILABLE);
        return ApiResponse.ofSuccess(ReservationConverter.toStateDto(r));
    }

    @PatchMapping("/{reservationId}:deny")
    @Operation(summary = "가게 예약 거절")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchDeny(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        Reservation r = commandService.updateReservationStatus(reservationId, DENIED);
        return ApiResponse.ofSuccess(ReservationConverter.toStateDto(r));
    }

    @PatchMapping("/{reservationId}:cancel")
    @Operation(summary = "가게 예약 취소")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> patchCancel(
            @PathVariable Long storeId,
            @PathVariable Long reservationId) {

        Reservation r = commandService.updateReservationStatus(reservationId, CANCELED);
        return ApiResponse.ofSuccess(ReservationConverter.toCancelDto(r, "STORE"));
    }

    @GetMapping("/history")
    @Operation(summary = "매장 예약 내역 조회")
    public ApiResponse<ReservationResponseDto.ReservationListDto> getHistory(
            @PathVariable Long storeId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {

        Page<Reservation> p = queryService.getReservationCalendar(storeId, page);
        return ApiResponse.ofSuccess(ReservationConverter.toListDto(p));
    }
}