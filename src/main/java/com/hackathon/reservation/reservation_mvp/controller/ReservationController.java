package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
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

    @GetMapping("/reservations")
    public List<StoreListResponseDto> getStoresWithReservations(@PathVariable Long userId) {
        List<Store> stores = reservationService.getStoresWithUserReservations(userId);
        List<StoreListResponseDto> response = new ArrayList<>();

        for (Store store : stores) {
            List<Reservation> reservations = store.getReservations();

            // userId로 필터링
            reservations.stream()
                    .filter(reservation -> reservation.getMember().getMemberId().equals(userId))
                    .findFirst() // 한 개만 응답으로 포함
                    .ifPresent(reservation -> {
                        LocalTime openTime = store.getSchedules().isEmpty() ? LocalTime.of(9, 0) : store.getSchedules().get(0).getOpenTime();
                        LocalTime closeTime = store.getSchedules().isEmpty() ? LocalTime.of(21, 0) : store.getSchedules().get(0).getCloseTime();

                        response.add(new StoreListResponseDto(store, reservation, openTime, closeTime));
                    });
        }

        return response;
    }

    @PostMapping("/reservations")
    public StoreReservationResponseDto reserveToAvailableStores(
            @PathVariable Long userId,
            @RequestBody StoreReservationRequestDto requestDto
    ) {
        int reservedCount = reservationService.reserveAllAvailableStores(userId, requestDto);
        return new StoreReservationResponseDto(reservedCount, reservedCount > 0 ?
                reservedCount + "개 매장에 예약이 완료되었습니다." : "예약 가능한 매장이 없습니다.");
    }

    @GetMapping("/stores/{storeId}")
    public StoreDetailResponseDto getStoreSimple(
            @PathVariable Long userId,
            @PathVariable Long storeId
    ) {
        return reservationService.getStoreBasicInfo(userId, storeId);
    }

}
