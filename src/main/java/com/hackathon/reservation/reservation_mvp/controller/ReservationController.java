package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 측 예약 관련 API
 */
@RestController
@RequestMapping("/v1/users/{userId}")
@RequiredArgsConstructor
public class ReservationController {

    @Qualifier("reservationQueryServiceImpl")
    private final ReservationQueryService queryService;

    @Qualifier("reservationCommandServiceImpl")
    private final ReservationCommandService commandService;


    @GetMapping("/reservations")
    @Operation(summary = "사용자 예약 목록 조회")
    public ApiResponse<List<StoreListResponseDto>> listUserReservations(
            @PathVariable Long userId) {

        List<Store> stores = queryService.getStoresWithUserReservations(userId);
        List<StoreListResponseDto> dtos = stores.stream()
                .map(s -> {
                    var reservation = s.getReservations().stream()
                            .filter(r -> r.getMember().getMemberId().equals(userId))
                            .findFirst()
                            .orElseThrow();
                    LocalTime open  = s.getSchedules().isEmpty()
                            ? LocalTime.of(9, 0)
                            : s.getSchedules().get(0).getOpenTime();
                    LocalTime close = s.getSchedules().isEmpty()
                            ? LocalTime.of(21, 0)
                            : s.getSchedules().get(0).getCloseTime();
                    return new StoreListResponseDto(s, reservation, open, close);
                })
                .collect(Collectors.toList());

        return ApiResponse.ofSuccess(dtos);
    }

    @GetMapping("/stores/{storeId}")
    @Operation(summary = "사용자 매장 상세 조회")
    public ApiResponse<StoreDetailResponseDto> getStoreDetail(
            @PathVariable Long userId,
            @PathVariable Long storeId) {

        return ApiResponse.ofSuccess(
                queryService.getStoreBasicInfo(userId, storeId)
        );
    }

    @PostMapping("/reservations")
    @Operation(summary = "사용자 예약 요청")
    public ApiResponse<Integer> reserveStores(
            @PathVariable Long userId,
            @RequestBody StoreReservationRequestDto dto) {

        int count = commandService.reserveAllAvailableStores(userId, dto);
        return ApiResponse.ofSuccess(count);
    }

    @PatchMapping("/reservations/{reservationId}/status")
    @Operation(summary = "예약 상태 업데이트")
    public ApiResponse<Void> updateStatus(
            @PathVariable Long userId,
            @PathVariable Long reservationId,
            @RequestParam ReservationStatus newStatus) {

        commandService.updateReservationStatus(reservationId, newStatus);
        return ApiResponse.ofSuccess(null);
    }
}