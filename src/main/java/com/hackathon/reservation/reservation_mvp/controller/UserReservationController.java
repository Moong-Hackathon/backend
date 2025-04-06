package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.ReservationConverter;
import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.service.StoreService;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.CANCELED;
import static com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus.CONFIRMED;

@RestController
@RequestMapping("/v1/members/{memberId}/reservations")
@RequiredArgsConstructor
public class UserReservationController {

    private final StoreService storeService;
    private final ReservationCommandService reservationCommandService;

    @GetMapping
    public List<StoreListResponseDto> getStoresWithReservations(@PathVariable Long memberId) {
        List<Store> stores = storeService.getStoresWithUserReservations(memberId);
        List<StoreListResponseDto> response = new ArrayList<>();

        for (Store store : stores) {

            store.getReservations()
                    .stream()
                    .filter(r -> r.getMember().getMemberId().equals(memberId))
                    .findFirst()
                    .ifPresent(reservation -> {
                        LocalTime openTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(9, 0)
                                : store.getSchedules().get(0).getOpenTime();
                        LocalTime closeTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(21, 0)
                                : store.getSchedules().get(0).getCloseTime();

                        response.add(new StoreListResponseDto(store, reservation, openTime, closeTime));
                    });
        }

        return response;
    }

    @PostMapping
    public StoreReservationResponseDto reserveToAvailableStores(
            @PathVariable Long userId,
            @RequestBody StoreReservationRequestDto requestDto
    ) {
        int reservedCount = storeService.reserveAllAvailableStores(userId, requestDto);
        return new StoreReservationResponseDto(reservedCount, reservedCount > 0 ?
                reservedCount + "개 매장에 예약이 완료되었습니다." : "예약 가능한 매장이 없습니다.");
    }

    @PatchMapping("/{reservationId}:confirm")
    @Operation(summary = "사용자 예약 확정 API", description = "점주가 예약 가능으로 승인한 예약(AVAILABLE)을 사용자가 최종 확정(CONFIRMED)합니다.")
    public ApiResponse<ReservationResponseDto.ReservationStateDto> patchReservationConfirmed(@PathVariable("memberId") Long memberId, @PathVariable("reservationId") Long reservationId) {
        Reservation reservation = reservationCommandService.patchReservationStatusByMember(memberId, reservationId, CONFIRMED);
        return ApiResponse.onSuccess(ReservationConverter.reservationStateDto(reservation));
    }

    @PatchMapping("/{reservationId}:calcel")
    @Operation(summary = "사용자 예약 취소 전환 API", description = "사용자가 이미 생성한 예약을 취소할 수 있으며, 예약 취소 시 취소 주체를 MEMBER로 기록합니다")
    public ApiResponse<ReservationResponseDto.ReservationStateCancelDto> patchReservationCancelByMember(@PathVariable("memberId") Long memberId, @PathVariable("reservationId") Long reservationId) {
        Reservation reservation = reservationCommandService.patchReservationStatusByMember(memberId, reservationId, CANCELED);
        return ApiResponse.onSuccess(ReservationConverter.reservationStateCancelDto(reservation,"MEMBER"));
    }

}
