package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class StoreListResponseDto {

    private Long storeId;
    private String storeName;
    private Double latitude;
    private Double longitude;
    private Integer capacity;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Long reservationId;
    private String reservationStatus;

    public StoreListResponseDto(Store store, Reservation reservation, LocalTime openTime, LocalTime closeTime) {
        this.storeId = store.getStoreId();
        this.storeName = store.getStoreName();
        this.latitude = store.getLatitude();
        this.longitude = store.getLongitude();
        this.capacity = store.getCapacity();
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.reservationId = reservation.getReservationId();
        this.reservationStatus = reservation.getStatus().name();
    }
}