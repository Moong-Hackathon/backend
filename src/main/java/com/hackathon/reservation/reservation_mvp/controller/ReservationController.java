package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/users/{userId}")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 목록 조회 API
     * GET /v1/users/{userId}/reservations
     */
    @GetMapping("/reservations")
    public List<StoreListResponseDto> getStoresWithReservations(@PathVariable Long userId) {
        List<Store> stores = reservationService.getStoresWithUserReservations(userId);
        List<StoreListResponseDto> response = new ArrayList<>();

        for (Store store : stores) {
            store.getReservations().stream()
                    .filter(reservation -> reservation.getMember().getMemberId().equals(userId))
                    .findFirst()
                    .ifPresent(reservation -> {
                        LocalTime openTime = store.getSchedules().isEmpty() ? LocalTime.of(9, 0) : store.getSchedules().get(0).getOpenTime();
                        LocalTime closeTime = store.getSchedules().isEmpty() ? LocalTime.of(21, 0) : store.getSchedules().get(0).getCloseTime();
                        response.add(new StoreListResponseDto(store, reservation, openTime, closeTime));
                    });
        }
        return response;
    }

    /**
     * 예약 요청 API
     * POST /v1/users/{userId}/reservations
     */
    @PostMapping("/reservations")
    public StoreReservationResponseDto reserveToAvailableStores(
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