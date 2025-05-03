package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.entity.StoreSchedule;
import com.hackathon.reservation.reservation_mvp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ReservationQueryService} 구현체.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryServiceImpl implements ReservationQueryService {

    private final StoreRepository storeRepository;

    @Override
    public List<Store> getStoresWithUserReservations(Long userId) {
        return storeRepository.findAll().stream()
                .filter(store -> store.getReservations().stream()
                        .anyMatch(r -> r.getMember().getMemberId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Override
    public StoreDetailResponseDto getStoreBasicInfo(Long userId, Long storeId) {
        Store store = storeRepository.findWithSchedulesById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));

        LocalTime open = store.getSchedules().stream()
                .findFirst()
                .map(StoreSchedule::getOpenTime)
                .orElse(LocalTime.of(10, 0));
        LocalTime close = store.getSchedules().stream()
                .findFirst()
                .map(StoreSchedule::getCloseTime)
                .orElse(LocalTime.of(22, 0));

        return StoreDetailResponseDto.from(store, open, close);
    }
}