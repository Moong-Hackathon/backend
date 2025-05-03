package com.hackathon.reservation.reservation_mvp.converter;

import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for converting {@link Reservation} entities into various
 * {@link ReservationResponseDto} types.
 */
@Component
public final class ReservationConverter {

    private ReservationConverter() {
        // Prevent instantiation
    }

    /**
     * Converts a single {@link Reservation} to its DTO form.
     *
     * @param reservation the reservation entity
     * @return the reservation DTO
     */
    public static ReservationResponseDto.ReservationDto toDto(
            final Reservation reservation) {
        return ReservationResponseDto.ReservationDto.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getMember().getMemberId())
                .userName(reservation.getMember().getName())
                .reservationTime(reservation.getReservationTime())
                .numberOfPeople(reservation.getNumberOfPeople())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    /**
     * Converts a paginated list of {@link Reservation} entities into a
     * {@link ReservationResponseDto.ReservationListDto}.
     *
     * @param page the page of reservations
     * @return the list DTO with pagination metadata
     */
    public static ReservationResponseDto.ReservationListDto toListDto(
            final Page<Reservation> page) {
        List<ReservationResponseDto.ReservationDto> list = page.stream()
                .map(ReservationConverter::toDto)
                .collect(Collectors.toList());

        return ReservationResponseDto.ReservationListDto.builder()
                .reservationList(list)
                .listSize(list.size())
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    /**
     * Builds a DTO for state-change responses (accept/deny) of a reservation.
     *
     * @param reservation the reservation entity
     * @return the state DTO
     */
    public static ReservationResponseDto.ReservationStateDto toStateDto(
            final Reservation reservation) {
        return ReservationResponseDto.ReservationStateDto.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .respondedAt(reservation.getCreatedAt())
                .build();
    }

    /**
     * Builds a DTO for cancellation responses of a reservation.
     *
     * @param reservation the reservation entity
     * @param canceledBy  who cancelled ("STORE" or "MEMBER")
     * @return the cancellation state DTO
     */
    public static ReservationResponseDto.ReservationStateCancelDto toCancelDto(
            final Reservation reservation, final String canceledBy) {
        return ReservationResponseDto.ReservationStateCancelDto.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .canceledBy(canceledBy)
                .canceledAt(reservation.getCreatedAt())
                .build();
    }
}