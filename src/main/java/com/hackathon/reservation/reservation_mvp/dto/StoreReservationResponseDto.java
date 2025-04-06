package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreReservationResponseDto {
    private int requestedStoreCount;
    private String message;
}
