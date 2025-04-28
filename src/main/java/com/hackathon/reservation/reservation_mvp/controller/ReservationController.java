package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/{userId}")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 요청 API
     * POST /v1/users/{userId}/reservations
     */
    @PostMapping("/reservations")
    public StoreReservationResponseDto reserveAndGetStores(
            @PathVariable Long userId,
            @RequestBody StoreReservationRequestDto requestDto
    ) {
        return reservationService.reserveAndGetAvailableStores(userId, requestDto);
    }

    /**
     * 매장 상세 조회 API
     * GET /v1/users/{userId}/stores/{storeId}
     */
    @GetMapping("/stores/{storeId}")
    public StoreDetailResponseDto getStoreSimple(
            @PathVariable Long userId,
            @PathVariable Long storeId
    ) {
        return reservationService.getStoreBasicInfo(userId, storeId);
    }
}