package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Read‐only operations for reservations and related store info.
 */
public interface ReservationQueryService {
    /** 점주용: 해당 매장의 모든(이번달) 예약 조회 */
    Page<Reservation> getReservations(Long storeId, int page);

    /** 점주용: 해당 매장의 CONFIRMED(역사) 예약 조회 */
    Page<Reservation> getReservationCalendar(Long storeId, int page);

    /** 사용자용: 해당 사용자가 예약한 모든 매장 조회 */
    List<Store> getStoresWithUserReservations(Long userId);

    /** 사용자용: 특정 매장 기본 정보 조회 */
    StoreDetailResponseDto getStoreBasicInfo(Long userId, Long storeId);
}