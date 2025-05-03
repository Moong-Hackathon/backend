package com.hackathon.reservation.reservation_mvp.converter;

import com.hackathon.reservation.reservation_mvp.dto.StoreResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import org.springframework.stereotype.Component;

/**
 * Utility for converting {@link Store} entities into DTOs.
 */
@Component
public final class StoreConverter {

    private StoreConverter() {
        // Prevent instantiation
    }

    /**
     * Converts a {@link Store} into a {@link StoreResponseDto.StoreStateDto}.
     *
     * @param store the store entity
     * @return the DTO reflecting reservation-open status
     */
    public static StoreResponseDto.StoreStateDto toStateDto(final Store store) {
        return StoreResponseDto.StoreStateDto.builder()
                .storeId(store.getStoreId())
                .isReservationOpen(store.getIsReservationOpen())
                .message("가게의 예약 가능 여부를 변경했습니다.")
                .build();
    }
}