package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.converter.StoreConverter;
import com.hackathon.reservation.reservation_mvp.dto.StoreRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/stores/{storeId}")
@RequiredArgsConstructor
public class StoreOwnerController {
    private final StoreService storeService;

    @PatchMapping("/status")
    @Operation(summary = "사장님 예약 받기 상태 변경 API", description = "사장님이 매장의 예약 오픈/닫힘 상태를 변경합니다.")
    public StoreResponseDto.StoreStateDto patchStoreStatus(
            @PathVariable("storeId") Long storeId,
            @RequestBody StoreRequestDto.StoreStateDto request
    ) {
        Store store = storeService.patchStoreStatus(storeId, request.getIsReservationOpen());
        return StoreConverter.storeStateDto(store);
    }
}
