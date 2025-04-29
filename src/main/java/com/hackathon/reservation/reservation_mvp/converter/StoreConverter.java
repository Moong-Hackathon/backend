package com.hackathon.reservation.reservation_mvp.converter;

import com.hackathon.reservation.reservation_mvp.dto.ReservationResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import org.springframework.stereotype.Component;

@Component
public class StoreConverter {
    public static StoreResponseDto.StoreStateDto storeStateDto(Store store) {
        return StoreResponseDto.StoreStateDto.builder()
                .storeId(store.getStoreId())
                .isReservationOpen(store.getIsReservationOpen())
                .message("가게의 예약 가능 여부를 변경했습니다.")
                .build();
    }
}
