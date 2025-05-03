package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTOs for store-owner operations.
 */
public class StoreRequestDto {

    /**
     * Payload for toggling a store’s reservation-open status.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreStateDto {
        private Boolean isReservationOpen;
    }
}