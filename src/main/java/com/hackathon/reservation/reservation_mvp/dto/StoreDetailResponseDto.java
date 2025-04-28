package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

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

    public static StoreDetailResponseDto from(Store store, LocalTime openTime, LocalTime closeTime) {
        return StoreDetailResponseDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .capacity(store.getCapacity())
                .openTime(openTime.toString())   // "10:00" 이런 String 형식
                .closeTime(closeTime.toString())
                .address(store.getAddress())     // Store에 address 필드 있다고 가정
                .mainImage(store.getMainImage()) // Store에 mainImage 필드 있다고 가정
                .menuImage(store.getMenuImages())// Store에 menuImages 필드(List<String>) 있다고 가정
                .build();
    }
}