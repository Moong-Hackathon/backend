package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReservationService handles business logic for reservations:
 * - Listing user reservations
 * - Searching available stores and creating reservations
 * - Fetching store details
 * - Updating reservation status and sending SSE notifications
 */
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

        return storeRepository.findAll().stream()
                .filter(store -> isWithinDistance(userLat, userLng, store.getLatitude(), store.getLongitude(), distanceType))
                .filter(store -> store.getCapacity() >= numberOfPeople)
                .filter(store -> isWithinOperatingTime(store, reservationTime.toLocalTime()))
                .filter(store -> isReservationSlotAvailable(store, reservationTime, numberOfPeople))
                .collect(Collectors.toList())
                .size();
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c; // km
    }

    private boolean isWithinDistance(double lat1, double lng1, double lat2, double lng2, String type) {
        double distance = calculateDistance(lat1, lng1, lat2, lng2);
        switch (type.toUpperCase()) {
            case "NEAR": return distance <= 1.0;
            case "MID": return distance <= 3.0;
            case "FAR": return distance <= 5.0;
            default: return false;
        }
    }

    private boolean isWithinOperatingTime(Store store, LocalTime reservationTime) {
        for (StoreSchedule schedule : store.getSchedules()) {
            if (!reservationTime.isBefore(schedule.getOpenTime())
                    && !reservationTime.isAfter(schedule.getCloseTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean isReservationSlotAvailable(Store store, LocalDateTime reservationTime, int numberOfPeople) {
        LocalDateTime start = reservationTime.minusHours(1);
        LocalDateTime end = reservationTime.plusHours(1);
        int reserved = store.getReservations().stream()
                .filter(r -> {
                    LocalDateTime t = r.getReservationTime();
                    return !t.isBefore(start) && !t.isAfter(end);
                })
                .mapToInt(Reservation::getNumberOfPeople)
                .sum();
        return (reserved + numberOfPeople) <= store.getCapacity();
    }

    /**
     * Creates reservations for all stores meeting criteria and returns detailed info
     */
    @Transactional
    public StoreReservationResponseDto reserveAndGetAvailableStores(Long userId, StoreReservationRequestDto dto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<StoreReservationResponseDto.StoreInfo> infos = storeRepository.findAll().stream()
                .filter(store -> isWithinDistance(dto.getLocation().getLatitude(), dto.getLocation().getLongitude(), store.getLatitude(), store.getLongitude(), dto.getDistanceType()))
                .filter(store -> store.getCapacity() >= dto.getNumberOfPeople())
                .filter(store -> isWithinOperatingTime(store, dto.getReservationTime().toLocalTime()))
                .filter(store -> isReservationSlotAvailable(store, dto.getReservationTime(), dto.getNumberOfPeople()))
                .map(store -> {
                    Reservation res = Reservation.builder()
                            .member(member)
                            .store(store)
                            .reservationTime(dto.getReservationTime())
                            .numberOfPeople(dto.getNumberOfPeople())
                            .status(ReservationStatus.PENDING)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    reservationRepository.save(res);

                    double dist = calculateDistance(dto.getLocation().getLatitude(), dto.getLocation().getLongitude(), store.getLatitude(), store.getLongitude());
                    LocalTime open = store.getSchedules().isEmpty() ? LocalTime.of(10,0) : store.getSchedules().get(0).getOpenTime();
                    LocalTime close = store.getSchedules().isEmpty() ? LocalTime.of(22,0) : store.getSchedules().get(0).getCloseTime();

                    return StoreReservationResponseDto.StoreInfo.builder()
                            .storeId(store.getStoreId())
                            .storeName(store.getStoreName())
                            .latitude(store.getLatitude())
                            .longitude(store.getLongitude())
                            .capacity(store.getCapacity())
                            .address(store.getAddress())
                            .openTime(open.atDate(dto.getReservationTime().toLocalDate()))
                            .closeTime(close.atDate(dto.getReservationTime().toLocalDate()))
                            .distance(String.format("%.1fkm", dist))
                            .mainImage(store.getMainImage())
                            .menuImages(store.getMenuImages())
                            .reservation(StoreReservationResponseDto.ReservationInfo.builder()
                                    .reservationId(res.getReservationId())
                                    .reservationTime(res.getReservationTime())
                                    .numberOfPeople(res.getNumberOfPeople())
                                    .status(res.getStatus().name())
                                    .canceledBy(res.getCanceledBy())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        return StoreReservationResponseDto.builder()
                .requestedStoreCount(infos.size())
                .stores(infos)
                .build();
    }

    /**
     * Fetch basic store details
     */
    public StoreDetailResponseDto getStoreBasicInfo(Long userId, Long storeId) {
        Store store = storeRepository.findWithSchedulesById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));
        LocalTime open = store.getSchedules().isEmpty() ? LocalTime.of(10,0) : store.getSchedules().get(0).getOpenTime();
        LocalTime close = store.getSchedules().isEmpty() ? LocalTime.of(22,0) : store.getSchedules().get(0).getCloseTime();
        return StoreDetailResponseDto.from(store, open, close);
    }

    /**
     * Update reservation status and notify via SSE
     */
    @Transactional
    public void updateReservationStatus(Long reservationId, String newStatus) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        res.setStatus(Enum.valueOf(ReservationStatus.class, newStatus));
        reservationRepository.save(res);
        notificationService.notifyReservationUpdate(res.getMember().getMemberId(), res);
    }
}