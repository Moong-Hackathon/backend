package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/stores/")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationQueryService reservationQueryService;

    @GetMapping("/{storeId}/reservations")
    @Operation(summary="매장 점주 예약 목록 조회 API", description="점주(매장 주인)가 본인 매장에 접수된 모든 예약 목록과 상태를 조회할 수 있도록 API를 구현합니다.")
    public ApiResponse<ReservationResponseDto.ReservationListDto> getReservations(@PathVariable("storeId") Long storeId, @RequestParam(name = "page",  defaultValue = "0") Integer page) {
        Page<Reservation> reservationList = reservationQueryService.getReservations(storeId, page);
        return ApiResponse.onSuccess(ReservationConverter.reservationListDto(reservationList));
    }


}
