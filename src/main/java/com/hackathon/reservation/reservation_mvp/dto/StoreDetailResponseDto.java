package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

/**
 * DTO for detailed store information in user-facing endpoints.
 */
@Getter
@Builder
public class StoreDetailResponseDto {
    private Long storeId;
    private String storeName;
    private double latitude;
    private double longitude;
    private int capacity;
    private String openTime;
    private String closeTime;
    private String address;
    private String mainImage;
    private List<String> menuImage;

    /**
     * Creates a StoreDetailResponseDto from the given entity and schedule times.
     *
     * @param store the store entity
     * @param openTime the opening time
     * @param closeTime the closing time
     * @return populated DTO
     */
    public static StoreDetailResponseDto from(
            Store store, LocalTime openTime, LocalTime closeTime) {
        return StoreDetailResponseDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .capacity(store.getCapacity())
                .openTime(openTime.toString())
                .closeTime(closeTime.toString())
                .address(store.getAddress())
                .mainImage(store.getMainImage())
                .menuImage(store.getMenuImages())
                .build();
    }
}