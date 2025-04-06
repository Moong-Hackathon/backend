package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/users/{userId}/reservations")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public List<StoreListResponseDto> getStoresWithReservations(@PathVariable Long userId) {
        List<Store> stores = storeService.getStoresWithUserReservations(userId);
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

    @PostMapping
    public StoreReservationResponseDto reserveToAvailableStores(
            @PathVariable Long userId,
            @RequestBody StoreReservationRequestDto requestDto
    ) {
        int reservedCount = storeService.reserveAllAvailableStores(userId, requestDto);
        return new StoreReservationResponseDto(reservedCount, reservedCount > 0 ?
                reservedCount + "개 매장에 예약이 완료되었습니다." : "예약 가능한 매장이 없습니다.");
    }

}
