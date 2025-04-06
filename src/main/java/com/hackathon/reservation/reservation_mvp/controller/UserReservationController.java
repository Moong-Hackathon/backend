package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.dto.StoreReservationResponseDto;
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
public class UserReservationController {

    private final StoreService storeService;

    @GetMapping
    public List<StoreReservationResponseDto> getStoresWithReservations(@PathVariable Long userId) {
        List<Store> stores = storeService.getStoresWithUserReservations(userId);
        List<StoreReservationResponseDto> response = new ArrayList<>();

        for (Store store : stores) {

            store.getReservations()
                    .stream()
                    .filter(r -> r.getMember().getMemberId().equals(userId))
                    .findFirst()
                    .ifPresent(reservation -> {
                        LocalTime openTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(9, 0)
                                : store.getSchedules().get(0).getOpenTime();
                        LocalTime closeTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(21, 0)
                                : store.getSchedules().get(0).getCloseTime();

                        response.add(new StoreReservationResponseDto(store, reservation, openTime, closeTime));
                    });
        }

        return response;
    }
}
