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

@RestController
@RequestMapping("/v1/stores/{storeId}/reservations")
@RequiredArgsConstructor
public class ReservationRestController {
    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;

    @GetMapping("")
    @Operation(summary = "매장 점주 예약 목록 조회 API", description = "점주(매장 주인)가 본인 매장에 접수된 모든 예약 목록과 상태를 조회할 수 있도록 API를 구현합니다.")
    public ApiResponse<ReservationResponseDto.ReservationListDto> getReservations(@PathVariable("storeId") Long storeId, @RequestParam(name = "page", defaultValue = "0") Integer page) {
        Page<Reservation> reservationList = reservationQueryService.getReservations(storeId, page);
        return ApiResponse.onSuccess(ReservationConverter.reservationListDto(reservationList));
    }

    @PatchMapping("/{reservationId}:accept")
    @Operation(summary = "가게의 예약 가능 전환 API", description = "점주가 고객의 예약 요청을 수락하여 예약 상태를 AVAILABLE로 변경합니다.")
    public ApiResponse<ReservationResponseDto.StoreReservationStateDto> patchReservationAccept(@PathVariable("storeId") Long storeId, @PathVariable("reservationId") Long reservationId) {
        Reservation reservation = reservationCommandService.patchReservationStatus(storeId, reservationId, AVAILABLE);
        return ApiResponse.onSuccess(ReservationConverter.storeReservationStateDto(reservation));
    }

    @PatchMapping("/{reservationId}:deny")
    @Operation(summary = "가게의 예약 가능 전환 API", description = "점주가 고객의 예약 요청을 거절하여 예약 상태를 DENIED로 변경합니다.")
    public ApiResponse<ReservationResponseDto.StoreReservationStateDto> patchReservationDeny(@PathVariable("storeId") Long storeId, @PathVariable("reservationId") Long reservationId) {
        Reservation reservation = reservationCommandService.patchReservationStatus(storeId, reservationId, DENIED);
        return ApiResponse.onSuccess(ReservationConverter.storeReservationStateDto(reservation));
    }

    @PatchMapping("/{reservationId}:calcel")
    @Operation(summary = "가게의 예약 취소 전환 API", description = "점주가 고객의 예약 요청을 거절하여 예약 상태를 CANCELED로 변경합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> patchReservationCancel(@PathVariable("storeId") Long storeId, @PathVariable("reservationId") Long reservationId) {
        Reservation reservation = reservationCommandService.patchReservationStatus(storeId, reservationId, CANCELED);
        return ApiResponse.onSuccess(ReservationConverter.reservationStateCancelDto(reservation,"STORE"));
    }

}

