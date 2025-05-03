package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.service.ReservationService;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for user-facing reservation queries.
 */
@RestController
@RequestMapping("/v1/users/{userId}")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationCommandService reservationCommandService;

    /**
     * Retrieves the list of stores reserved by a given user.
     *
     * @param userId the ID of the user
     * @return an {@link ApiResponse} wrapping a list of {@link StoreListResponseDto}
     */
    @GetMapping("/reservations")
    @Operation(
            summary = "User reservation list API",
            description = "Fetches the list of stores that the user has reserved along with their reservation info."
    )
    public ApiResponse<List<StoreListResponseDto>> getUserReservations(
            @PathVariable Long userId) {
        List<StoreListResponseDto> dtoList = new ArrayList<>();
        for (Store store : reservationService.getStoresWithUserReservations(userId)) {
            store.getReservations().stream()
                    .filter(r -> r.getMember().getMemberId().equals(userId))
                    .findFirst()
                    .ifPresent(reservation -> {
                        LocalTime openTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(9, 0)
                                : store.getSchedules().get(0).getOpenTime();
                        LocalTime closeTime = store.getSchedules().isEmpty()
                                ? LocalTime.of(21, 0)
                                : store.getSchedules().get(0).getCloseTime();
                        dtoList.add(new StoreListResponseDto(store, reservation, openTime, closeTime));
                    });
        }
        return ApiResponse.ofSuccess(dtoList);
    }

    /**
     * Retrieves basic information about a store for a given user.
     *
     * @param userId  the ID of the user
     * @param storeId the ID of the store
     * @return an {@link ApiResponse} wrapping {@link StoreDetailResponseDto}
     */
    @GetMapping("/stores/{storeId}")
    @Operation(
            summary = "User store detail API",
            description = "Fetches basic details of a specific store."
    )
    public ApiResponse<StoreDetailResponseDto> getStoreDetail(
            @PathVariable Long userId,
            @PathVariable Long storeId) {
        StoreDetailResponseDto detail = reservationService.getStoreBasicInfo(userId, storeId);
        return ApiResponse.ofSuccess(detail);
    }
}