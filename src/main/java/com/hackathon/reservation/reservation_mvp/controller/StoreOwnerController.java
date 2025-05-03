package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.converter.StoreConverter;
import com.hackathon.reservation.reservation_mvp.dto.StoreRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Allows store owners to update store settings (e.g. open/close reservations).
 */
@RestController
@RequestMapping("/v1/stores/{storeId}")
@RequiredArgsConstructor
public class StoreOwnerController {

    private final StoreService storeService;

    /**
     * Toggles whether the store is accepting reservations.
     */
    @PatchMapping("/status")
    @Operation(summary = "사장 예약 상태 변경 API",
            description = "매장의 예약 가능 여부를 변경합니다.")
    public ApiResponse<StoreResponseDto.StoreStateDto> patchStoreStatus(
            @PathVariable Long storeId,
            @RequestBody StoreRequestDto.StoreStateDto request) {

        Store updated = storeService.patchStoreStatus(storeId, request.getIsReservationOpen());
        // <-- now calls toStateDto(...)
        return ApiResponse.ofSuccess(StoreConverter.toStateDto(updated));
    }
}