package com.hackathon.reservation.reservation_mvp.service.reservation.impl;

import com.hackathon.reservation.reservation_mvp.apipayload.code.status.ErrorStatus;
import com.hackathon.reservation.reservation_mvp.apipayload.exception.GeneralException;
import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.entity.StoreSchedule;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import com.hackathon.reservation.reservation_mvp.repository.StoreRepository;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Query‐side 구현체. {@link ReservationQueryService} 에 선언된
 * 조회 메서드만 책임집니다.
 */
@Service("reservationQueryServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryServiceImpl implements ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    @Override
    public Page<Reservation> getReservations(Long storeId, int page) {
        if (!storeRepository.existsById(storeId)) {
            throw new GeneralException(ErrorStatus.STORE_NOT_FOUND);
        }
        return reservationRepository.findByStore_StoreId(
                storeId, PageRequest.of(page, 10));
    }

    @Override
    public Page<Reservation> getReservationCalendar(Long storeId, int page) {
        if (!storeRepository.existsById(storeId)) {
            throw new GeneralException(ErrorStatus.STORE_NOT_FOUND);
        }
        return reservationRepository.findByStore_StoreIdAndStatus(
                storeId, ReservationStatus.CONFIRMED, PageRequest.of(page, 10));
    }

    @Override
    public List<Store> getStoresWithUserReservations(Long userId) {
        return storeRepository.findAll().stream()
                .filter(s -> s.getReservations().stream()
                        .anyMatch(r -> r.getMember().getMemberId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Override
    public StoreDetailResponseDto getStoreBasicInfo(Long userId, Long storeId) {
        Store store = storeRepository.findWithSchedulesById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));

        LocalTime open = store.getSchedules().stream()
                .map(StoreSchedule::getOpenTime)
                .findFirst()
                .orElse(LocalTime.of(10, 0));
        LocalTime close = store.getSchedules().stream()
                .map(StoreSchedule::getCloseTime)
                .findFirst()
                .orElse(LocalTime.of(22, 0));

        return StoreDetailResponseDto.from(store, open, close);
    }
}