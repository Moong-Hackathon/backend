package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDetailForReservationDto {
    private Long storeId;
    private String storeName;
    private Double latitude;
    private Double longitude;
    private int capacity;
    private String openTime;
    private String closeTime;
    private String address;
    private String distance;  // 계산해서 넣기 (ex. "1.2km")
    private String mainImage;
    private List<String> menuImage;
    private ReservationInfoDto reservations;
}

