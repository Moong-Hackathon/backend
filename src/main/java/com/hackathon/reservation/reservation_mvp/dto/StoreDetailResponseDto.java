package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class StoreDetailResponseDto {
    private Long storeId;
    private String storeName;
    private double latitude;
    private double longitude;
    private int capacity;
    private LocalTime openTime;
    private LocalTime closeTime;

    public static StoreDetailResponseDto from(Store store, LocalTime openTime, LocalTime closeTime) {
        return new StoreDetailResponseDto(
                store.getStoreId(),
                store.getStoreName(),
                store.getLatitude(),
                store.getLongitude(),
                store.getCapacity(),
                openTime,
                closeTime
        );
    }
}