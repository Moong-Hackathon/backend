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

    /**
     * [사용자] 예약 확정 API
     * PATCH /v1/users/{userId}/reservations/{reservationId}:confirm
     */
    @PatchMapping("/reservations/{reservationId}:confirm")
    @Operation(summary = "사용자 예약 확정 API", description = "점주가 예약 가능(AVAILABLE)으로 승인한 예약을 사용자가 최종 확정(CONFIRMED)합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchReservationConfirmed(
            @PathVariable("userId") Long userId,
            @PathVariable("reservationId") Long reservationId
    ) {
        Reservation reservation = reservationCommandService.patchReservationStatusByMember(userId, reservationId, CONFIRMED);
        return ApiResponse.onSuccess(ReservationConverter.reservationStateDto(reservation));
    }

    /**
     * [사용자] 예약 취소 API
     * PATCH /v1/users/{userId}/reservations/{reservationId}:cancel
     */
    @PatchMapping("/reservations/{reservationId}:cancel")
    @Operation(summary = "사용자 예약 취소 전환 API", description = "사용자가 이미 생성한 예약을 취소할 수 있으며, 취소 주체를 MEMBER로 기록합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> patchReservationCancelByMember(
            @PathVariable("userId") Long userId,
            @PathVariable("reservationId") Long reservationId
    ) {
        Reservation reservation = reservationCommandService.patchReservationStatusByMember(userId, reservationId, CANCELED);
        return ApiResponse.onSuccess(ReservationConverter.reservationStateCancelDto(reservation, "MEMBER"));
    }
}
