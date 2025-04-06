package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class StoreReservationResponseDto {

    private Long storeId;
    private String storeName;
    private Double latitude;
    private Double longitude;
    private Integer capacity;
    private String openTime;
    private String closeTime;
    private ReservationDto reservation;

    public StoreReservationResponseDto(Store store, Reservation reservation, LocalTime openTime, LocalTime closeTime) {
        this.storeId = store.getStoreId();
        this.storeName = store.getStoreName();
        this.latitude = store.getLatitude();
        this.longitude = store.getLongitude();
        this.capacity = store.getCapacity();
        this.openTime = openTime.toString();
        this.closeTime = closeTime.toString();
        this.reservation = new ReservationDto(reservation);
    }

    @Data
    public static class ReservationDto {
        private Long reservationId;
        private String reservationTime;
        private Integer numberOfPeople;
        private String status;
        private String canceledBy;

        public ReservationDto(Reservation reservation) {
            this.reservationId = reservation.getReservationId();
            this.reservationTime = reservation.getReservationTime().toString();
            this.numberOfPeople = reservation.getNumberOfPeople();
            this.status = reservation.getStatus().toString();
            this.canceledBy = reservation.getCanceledBy();
        }
    }
}
