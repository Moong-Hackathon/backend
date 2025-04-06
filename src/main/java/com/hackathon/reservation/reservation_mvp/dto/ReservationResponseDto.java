package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReservationDto{
        private Long reservationId;
        private Long userId;
        private LocalDateTime reservationTime;
        private Integer numberOfPeople;
        private ReservationStatus status;
        private LocalDateTime createdAt;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReservationListDto{
        private List<ReservationDto> reservationList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

}
