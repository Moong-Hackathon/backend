package com.hackathon.reservation.reservation_mvp.dto;

import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTOs for reservation-related API responses.
 */
public class ReservationResponseDto {

    /**
     * Single reservation info for store-owner views.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationDto {
        private Long reservationId;
        private Long userId;
        private String userName;
        private LocalDateTime reservationTime;
        private Integer numberOfPeople;
        private ReservationStatus status;
        private LocalDateTime createdAt;
    }

    /**
     * Paged list of reservations with metadata.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationListDto {
        private List<ReservationDto> reservationList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    /**
     * Response when a reservation state is changed (accept/deny).
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationStateDto {
        private Long reservationId;
        private ReservationStatus status;
        private LocalDateTime respondedAt;
    }

    /**
     * Response when a reservation is canceled.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationStateCancelDto {
        private Long reservationId;
        private ReservationStatus status;
        private String canceledBy;
        private LocalDateTime canceledAt;
    }
}