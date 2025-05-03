package com.hackathon.reservation.reservation_mvp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Request payload when a user submits a reservation search.
 */
@Getter
@Setter
public class StoreReservationRequestDto {
    private Long userId;
    private Location location;
    private String distanceType;
    private Integer numberOfPeople;
    private LocalDateTime reservationTime;

    /**
     * Encapsulates user’s GPS coordinates.
     */
    @Getter
    @Setter
    public static class Location {
        private Double latitude;
        private Double longitude;
    }
}