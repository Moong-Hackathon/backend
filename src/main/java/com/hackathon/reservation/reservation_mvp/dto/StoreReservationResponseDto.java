package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreReservationResponseDto {
    private int requestedStoreCount;
    private List<StoreInfo> stores; // available store 목록

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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
        private ReservationInfo reservation;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReservationInfo {
        private Long reservationId;
        private LocalDateTime reservationTime;
        private int numberOfPeople;
        private String status;      // AVAILABLE, DENIED 등
        private String canceledBy;  // null 가능
    }
}
