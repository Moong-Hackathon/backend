package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.*;
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
    public List<StoreListResponseDto> getStoresWithReservations(@PathVariable Long userId) {
        // 사용자가 예약한 매장 목록 가져오기
        List<Store> stores = reservationService.getStoresWithUserReservations(userId);
        List<StoreListResponseDto> response = new ArrayList<>();

        for (Store store : stores) {
            store.getReservations().stream()
                    .filter(reservation -> reservation.getMember().getMemberId().equals(userId))
                    .findFirst()
                    .ifPresent(reservation -> {
                        // 매장 운영 시간이 있다면 그 시간을 사용, 없다면 09:00~21:00으로 가정
                        LocalTime openTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(9, 0)
                                : store.getSchedules().get(0).getOpenTime();
                        LocalTime closeTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(21, 0)
                                : store.getSchedules().get(0).getCloseTime();

                        // 응답용 DTO에 담아서 리턴
                        response.add(new StoreListResponseDto(store, reservation, openTime, closeTime));
                    });
        }
        return response;
    }

    /**
     * [사용자] 예약 요청 API
     * POST /v1/users/{userId}/reservations
     */
    @PostMapping("/reservations")
    public StoreReservationResponseDto reserveToAvailableStores(
            @PathVariable Long userId,
            @RequestBody StoreReservationRequestDto requestDto
    ) {
        int reservedCount = reservationService.reserveAllAvailableStores(userId, requestDto);
        return new StoreReservationResponseDto(
                reservedCount,
                reservedCount > 0
                        ? reservedCount + "개 매장에 예약이 완료되었습니다."
                        : "예약 가능한 매장이 없습니다."
        );
    }

    /**
     * [사용자] 매장 상세 조회 API
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