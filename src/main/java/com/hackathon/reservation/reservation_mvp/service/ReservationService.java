package com.hackathon.reservation.reservation_mvp.service;

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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationNotificationService notificationService;

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    private boolean isWithinOperatingTime(Store store, LocalTime time) {
        for (StoreSchedule sched : store.getSchedules()) {
            if (!time.isBefore(sched.getOpenTime()) && !time.isAfter(sched.getCloseTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean isReservationSlotAvailable(Store store, LocalDateTime resTime, int party) {
        LocalDateTime start = resTime.minusHours(1);
        LocalDateTime end = resTime.plusHours(1);
        int used = store.getReservations().stream()
            .filter(r -> {
                LocalDateTime t = r.getReservationTime();
                return !t.isBefore(start) && !t.isAfter(end);
            })
            .mapToInt(Reservation::getNumberOfPeople)
            .sum();
        return (used + party) <= store.getCapacity();
    }

    @Transactional
    public StoreReservationResponseDto reserveAndGetAvailableStores(Long userId, StoreReservationRequestDto req) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<StoreReservationResponseDto.StoreInfo> storeInfoList = storeRepository.findAll().stream()
            .filter(store -> isWithinOperatingTime(store, req.getReservationTime().toLocalTime()))
            .filter(store -> isReservationSlotAvailable(store, req.getReservationTime(), req.getNumberOfPeople()))
            .filter(store -> store.getCapacity() >= req.getNumberOfPeople())
            .map(store -> {
                // 1) Save reservation
                Reservation r = Reservation.builder()
                    .member(member)
                    .store(store)
                    .reservationTime(req.getReservationTime())
                    .numberOfPeople(req.getNumberOfPeople())
                    .status(ReservationStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
                reservationRepository.save(r);

                // 2) Build DTO
                double dist = calculateDistance(
                    req.getLocation().getLatitude(),
                    req.getLocation().getLongitude(),
                    store.getLatitude(),
                    store.getLongitude()
                );

                LocalTime open = store.getSchedules().isEmpty()
                    ? LocalTime.of(10, 0)
                    : store.getSchedules().get(0).getOpenTime();
                LocalTime close = store.getSchedules().isEmpty()
                    ? LocalTime.of(22, 0)
                    : store.getSchedules().get(0).getCloseTime();

                return StoreReservationResponseDto.StoreInfo.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .latitude(store.getLatitude())
                    .longitude(store.getLongitude())
                    .capacity(store.getCapacity())
                    .address(store.getAddress())
                    .openTime(open.atDate(req.getReservationTime().toLocalDate()))
                    .closeTime(close.atDate(req.getReservationTime().toLocalDate()))
                    .distance(String.format("%.1fkm", dist))
                    .mainImage(store.getMainImage())
                    .menuImages(store.getMenuImages())
                    .reservation(
                        StoreReservationResponseDto.ReservationInfo.builder()
                            .reservationId(r.getReservationId())
                            .reservationTime(r.getReservationTime())
                            .numberOfPeople(r.getNumberOfPeople())
                            .status(r.getStatus().name())
                            .canceledBy(r.getCanceledBy())
                            .build()
                    )
                    .build();
            })
            .collect(Collectors.toList());

        return StoreReservationResponseDto.builder()
            .requestedStoreCount(storeInfoList.size())
            .stores(storeInfoList)
            .build();
    }
}
