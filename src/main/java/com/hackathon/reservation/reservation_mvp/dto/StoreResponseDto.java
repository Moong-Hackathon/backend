package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StoreResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StoreStateDto{
        private Long storeId;
        private Boolean isReservationOpen;
        private String message;

    }
}
