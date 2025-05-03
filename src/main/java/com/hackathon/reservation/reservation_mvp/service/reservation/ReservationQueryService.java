package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;

import java.util.List;

/**
 * 조회 전용 서비스 (Query).
 * 사용자·점주용 읽기 작업만 담당합니다.
 */
public interface ReservationQueryService {

    /**
     * 사용자가 예약한 매장 목록을 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 매장 목록
     */
    List<Store> getStoresWithUserReservations(Long userId);

    /**
     * 특정 매장의 기본 정보를 조회합니다.
     *
     * @param userId  사용자 ID (미래 확장용)
     * @param storeId 매장 ID
     * @return 매장 상세 DTO
     */
    StoreDetailResponseDto getStoreBasicInfo(Long userId, Long storeId);

}