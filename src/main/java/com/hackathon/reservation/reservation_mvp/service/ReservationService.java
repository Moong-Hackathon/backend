package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.dto.ReservationEvent;
import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.entity.Member;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.entity.StoreSchedule;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.repository.MemberRepository;
import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import com.hackathon.reservation.reservation_mvp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    private final ReservationNotificationService notificationService;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public List<Store> getStoresWithUserReservations(Long userId) {
        return storeRepository.findAll().stream()
                .filter(store -> store.getReservations().stream()
                        .anyMatch(reservation -> reservation.getMember().getMemberId().equals(userId)))
                .collect(Collectors.toList());
    }

    public int checkReservationAvailability(Long userId, StoreReservationRequestDto dto) {
        double userLat = dto.getLocation().getLatitude();
        double userLng = dto.getLocation().getLongitude();
        String distanceType = dto.getDistanceType();
        int numberOfPeople = dto.getNumberOfPeople();
        LocalDateTime reservationTime = dto.getReservationTime();

        List<Store> allStores = storeRepository.findAll();

        List<Store> availableStores = allStores.stream()
                .filter(store -> isWithinDistance(userLat, userLng, store.getLatitude(), store.getLongitude(), distanceType))
                .filter(store -> store.getCapacity() >= numberOfPeople)
                .filter(store -> isWithinOperatingTime(store, reservationTime.toLocalTime()))
                .filter(store -> isReservationSlotAvailable(store, reservationTime, numberOfPeople))
                .collect(Collectors.toList());

        return availableStores.size();
    }

    private boolean isWithinDistance(double lat1, double lng1, double lat2, double lng2, String type) {
        double distance = Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2)); // 간단한 유클리드 거리 계산
        if ("NEAR".equalsIgnoreCase(type)) {
            return distance < 0.0063; // 유클리드 거리 700m 기준
        } else {
            return distance < 0.015;
        }
    }

    private boolean isWithinOperatingTime(Store store, LocalTime reservationTime) {
        if (store.getSchedules().isEmpty()) return false;
        for (StoreSchedule schedule : store.getSchedules()) {
            if (!reservationTime.isBefore(schedule.getOpenTime()) &&
                    !reservationTime.isAfter(schedule.getCloseTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean isReservationSlotAvailable(Store store, LocalDateTime reservationTime, int numberOfPeople) {
        LocalDateTime oneHourBefore = reservationTime.minusHours(1);
        LocalDateTime oneHourAfter = reservationTime.plusHours(1);

        int reservedCount = store.getReservations().stream()
                .filter(r -> {
                    LocalDateTime existingTime = r.getReservationTime();
                    return !existingTime.isBefore(oneHourBefore) && !existingTime.isAfter(oneHourAfter);
                })
                .mapToInt(Reservation::getNumberOfPeople)
                .sum();

        return (reservedCount + numberOfPeople) <= store.getCapacity();
    }

    @Transactional
    public int reserveAllAvailableStores(Long userId, StoreReservationRequestDto dto) {
        double userLat = dto.getLocation().getLatitude();
        double userLng = dto.getLocation().getLongitude();
        String distanceType = dto.getDistanceType();
        int numberOfPeople = dto.getNumberOfPeople();
        LocalDateTime reservationTime = dto.getReservationTime();

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Store> allStores = storeRepository.findAll();

        List<Store> availableStores = allStores.stream()
                .filter(store -> isWithinDistance(userLat, userLng, store.getLatitude(), store.getLongitude(), distanceType))
                .filter(store -> store.getCapacity() >= numberOfPeople)
                .filter(store -> isWithinOperatingTime(store, reservationTime.toLocalTime()))
                .filter(store -> isReservationSlotAvailable(store, reservationTime, numberOfPeople))
                .collect(Collectors.toList());

        for (Store store : availableStores) {
            Reservation reservation = Reservation.builder()
                    .member(member)
                    .store(store)
                    .reservationTime(reservationTime)
                    .numberOfPeople(numberOfPeople)
                    .status(ReservationStatus.PENDING)  // 기본 상태로 예약 생성
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            reservationRepository.save(reservation);
        }
        return availableStores.size();
    }

    public StoreDetailResponseDto getStoreBasicInfo(Long userId, Long storeId) {
        Store store = storeRepository.findWithSchedulesById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));
        LocalTime openTime = store.getSchedules().isEmpty() ? LocalTime.of(10, 0) : store.getSchedules().get(0).getOpenTime();
        LocalTime closeTime = store.getSchedules().isEmpty() ? LocalTime.of(22, 0) : store.getSchedules().get(0).getCloseTime();
        return StoreDetailResponseDto.from(store, openTime, closeTime);
    }

    @Transactional
    public void updateReservationStatus(Long reservationId, String newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(Enum.valueOf(ReservationStatus.class, newStatus));
        reservationRepository.save(reservation);

        ReservationEvent event = new ReservationEvent(
                reservation.getReservationId(),
                reservation.getStore().getStoreId(),
                newStatus,
                LocalDateTime.now()
        );
        Long userId = reservation.getMember().getMemberId();
        notificationService.notifyReservationUpdate(userId, event);
    }
}