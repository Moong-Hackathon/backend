package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response DTOs for store-owner APIs.
 */
public class StoreResponseDto {

    /**
     * DTO returned when store-owner toggles reservation availability.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreStateDto {
        private Long storeId;
        private Boolean isReservationOpen;
        private String message;
    }
}