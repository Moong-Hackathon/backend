package com.hackathon.reservation.reservation_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInfoDto {
    private Long reservationId;
    private LocalDateTime reservationTime;
    private int numberOfPeople;
    private String status; // AVAILABLE, DENIED 등
    private String canceledBy; // null일 수도 있음
}
