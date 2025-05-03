package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.service.ReservationService;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.CANCELED;
import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.CONFIRMED;

@RestController
@RequestMapping("/v1/users/{userId}")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationCommandService reservationCommandService;

    /**
     * [사용자] 예약 목록 조회 API
     * GET /v1/users/{userId}/reservations
     */
    @GetMapping("/reservations")
    @Operation(summary = "사용자 예약 목록 조회 API",
            description = "사용자가 예약한 매장 목록과 그 예약 정보를 조회합니다.")
    public ApiResponse<List<StoreListResponseDto>> getStoresWithReservations(@PathVariable Long userId) {
        List<StoreListResponseDto> dtoList = new ArrayList<>();
        for (Store store : reservationService.getStoresWithUserReservations(userId)) {
            store.getReservations().stream()
                    .filter(r -> r.getMember().getMemberId().equals(userId))
                    .findFirst()
                    .ifPresent(reservation -> {
                        LocalTime openTime  = store.getSchedules().isEmpty()
                                ? LocalTime.of(9, 0)
                                : store.getSchedules().get(0).getOpenTime();
                        LocalTime closeTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(21, 0)
                                : store.getSchedules().get(0).getCloseTime();
                        dtoList.add(new StoreListResponseDto(store, reservation, openTime, closeTime));
                    });
        }
        return ApiResponse.onSuccess(dtoList);
    }

    /**
     * [사용자] 매장 상세 조회 API
     * GET /v1/users/{userId}/stores/{storeId}
     */
    @GetMapping("/stores/{storeId}")
    @Operation(summary = "사용자 매장 상세 조회 API",
            description = "특정 매장의 기본 정보를 조회합니다.")
    public ApiResponse<StoreDetailResponseDto> getStoreDetail(
            @PathVariable Long userId,
            @PathVariable Long storeId
    ) {
        StoreDetailResponseDto detail = reservationService.getStoreBasicInfo(userId, storeId);
        return ApiResponse.onSuccess(detail);
    }
}