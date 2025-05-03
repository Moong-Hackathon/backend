package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response payload when user requests reservations across multiple stores.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreReservationResponseDto {
    private int requestedStoreCount;
    private List<StoreInfo> stores;

    /**
     * Info about each store where reservation was placed.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreInfo {
        private Long storeId;
        private String storeName;
        private Double latitude;
        private Double longitude;
        private int capacity;
        private String address;
        private LocalDateTime openTime;
        private LocalDateTime closeTime;
        private String distance;
        private String mainImage;
        private List<String> menuImages;
        private ReservationResponseDto.ReservationDto reservation;
    }
}