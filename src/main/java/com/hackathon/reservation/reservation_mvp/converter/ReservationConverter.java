package com.hackathon.reservation.reservation_mvp.converter;

import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationConverter {
    public static ReservationResponseDto.ReservationDto reservationDto(Reservation reservation) {
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

    public static ReservationResponseDto.ReservationListDto reservationListDto(Page<Reservation> reservationPage) {
        List<ReservationResponseDto.ReservationDto> dtoList = reservationPage.stream()
                .map(ReservationConverter::reservationDto)
                .collect(Collectors.toList());

        return ReservationResponseDto.ReservationListDto.builder()
                .reservationList(dtoList)
                .listSize(dtoList.size())
                .totalPage(reservationPage.getTotalPages())
                .totalElements(reservationPage.getTotalElements())
                .isFirst(reservationPage.isFirst())
                .isLast(reservationPage.isLast())
                .build();
    }

    public static ReservationResponseDto.ReservationStateDto reservationStateDto(Reservation reservation) {
        return ReservationResponseDto.ReservationStateDto.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .respondedAt(reservation.getCreatedAt())
                .build();
    }

    public static ReservationResponseDto.ReservationStateCancelDto reservationStateCancelDto(Reservation reservation, String by){
        return ReservationResponseDto.ReservationStateCancelDto.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .canceledBy(by)
                .canceledAt(reservation.getCreatedAt())
                .build();
    }


}
