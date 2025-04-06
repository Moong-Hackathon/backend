package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponseDto {

    //(사장님) 예약 목록 조회시 사용
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

    //가게의 예약 상태 전환(가능, 불가, 확정시 사용)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReservationStateDto{
        private Long reservationId;
        private ReservationStatus status;
        private LocalDateTime respondedAt;
    }

    //(사장님, 손님 공통)가게의 예약 상태 취소로 전환
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReservationStateCancelDto{
        private Long reservationId;
        private ReservationStatus status;
        private String canceledBy;
        private LocalDateTime canceledAt;
    }

}
