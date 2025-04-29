package com.hackathon.reservation.reservation_mvp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoreReservationRequestDto {
    private Long userId;
    private Location location;
    private String distanceType; // "NEAR" 또는 "FAR" 또는 "MID"
    private Integer numberOfPeople;
    private LocalDateTime reservationTime;

    @Data
    public static class Location {
        private Double latitude;
        private Double longitude;
    }
}
