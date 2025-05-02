package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.dto.ReservationInfoDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreDetailResponseDto;
import com.hackathon.reservation.reservation_mvp.dto.StoreListResponseDto;
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
import com.hackathon.reservation.reservation_mvp.service.ReservationNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationNotificationService notificationService;

    /**
     * 사용자 예약 목록 조회
     */
    public List<StoreListResponseDto> getStoresWithUserReservations(Long userId) {
        return storeRepository.findAll().stream()
                .filter(store -> store.getReservations().stream()
                        .anyMatch(r -> r.getMember().getMemberId().equals(userId)))
                .map(store -> {
                    Reservation r = store.getReservations().stream()
                            .filter(res -> res.getMember().getMemberId().equals(userId))
                            .findFirst().get();
                    LocalTime open = store.getSchedules().isEmpty()
                            ? LocalTime.of(9, 0)
                            : store.getSchedules().get(0).getOpenTime();
                    LocalTime close = store.getSchedules().isEmpty()
                            ? LocalTime.of(21, 0)
                            : store.getSchedules().get(0).getCloseTime();
                    return new StoreListResponseDto(store, r, open, close);
                })
                .collect(Collectors.toList());
    }

    /**
     * 매장 기본 정보 조회
     */
    public StoreDetailResponseDto getStoreBasicInfo(Long userId, Long storeId) {
        Store store = storeRepository.findWithSchedulesById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));
        LocalTime open = store.getSchedules().isEmpty()
                ? LocalTime.of(10, 0)
                : store.getSchedules().get(0).getOpenTime();
        LocalTime close = store.getSchedules().isEmpty()
                ? LocalTime.of(22, 0)
                : store.getSchedules().get(0).getCloseTime();
        return StoreDetailResponseDto.from(store, open, close);
    }

    /**
     * 사용자 예약 요청 및 결과 반환
     */
    @Transactional
    public StoreReservationResponseDto reserveAndGetAvailableStores(Long userId, StoreReservationRequestDto dto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        double userLat = dto.getLocation().getLatitude();
        double userLng = dto.getLocation().getLongitude();
        String type = dto.getDistanceType();
        int people = dto.getNumberOfPeople();
        LocalDateTime time = dto.getReservationTime();

        List<Store> available = storeRepository.findAll().stream()
                .filter(s -> isWithinDistance(userLat, userLng, s.getLatitude(), s.getLongitude(), type))
                .filter(s -> s.getCapacity() >= people)
                .filter(s -> isWithinOperatingTime(s, time.toLocalTime()))
                .filter(s -> isReservationSlotAvailable(s, time, people))
                .collect(Collectors.toList());

        List<StoreReservationResponseDto.StoreInfo> infos = new ArrayList<>();
        for (Store store : available) {
            Reservation res = Reservation.builder()
                    .member(member)
                    .store(store)
                    .reservationTime(time)
                    .numberOfPeople(people)
                    .status(ReservationStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            reservationRepository.save(res);

            double dist = calculateDistance(userLat, userLng, store.getLatitude(), store.getLongitude());
            LocalTime open = store.getSchedules().isEmpty()
                    ? LocalTime.of(10, 0)
                    : store.getSchedules().get(0).getOpenTime();
            LocalTime close = store.getSchedules().isEmpty()
                    ? LocalTime.of(22, 0)
                    : store.getSchedules().get(0).getCloseTime();

            ReservationInfoDto infoDto = new ReservationInfoDto(
                    res.getReservationId(),
                    res.getReservationTime(),
                    res.getNumberOfPeople(),
                    res.getStatus().name(),
                    res.getCanceledBy()
            );

            infos.add(StoreReservationResponseDto.StoreInfo.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .latitude(store.getLatitude())
                    .longitude(store.getLongitude())
                    .capacity(store.getCapacity())
                    .address(store.getAddress())
                    .openTime(open.atDate(time.toLocalDate()))
                    .closeTime(close.atDate(time.toLocalDate()))
                    .distance(String.format("%.1fkm", dist))
                    .mainImage(store.getMainImage())
                    .menuImages(store.getMenuImages())
                    .reservation(infoDto)
                    .build());
        }

        return StoreReservationResponseDto.builder()
                .requestedStoreCount(infos.size())
                .stores(infos)
                .build();
    }

    // --- helper methods ---
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    private boolean isWithinDistance(double lat1, double lng1, double lat2, double lng2, String type) {
        double distance = calculateDistance(lat1, lng1, lat2, lng2);
        return switch (type.toUpperCase()) {
            case "NEAR" -> distance <= 1.0;
            case "MID" -> distance <= 3.0;
            case "FAR" -> distance <= 5.0;
            default -> false;
        };
    }

    private boolean isWithinOperatingTime(Store store, LocalTime time) {
        for (StoreSchedule sch : store.getSchedules()) {
            if (!time.isBefore(sch.getOpenTime()) && !time.isAfter(sch.getCloseTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean isReservationSlotAvailable(Store store, LocalDateTime time, int people) {
        LocalDateTime before = time.minusHours(1);
        LocalDateTime after = time.plusHours(1);
        int sum = store.getReservations().stream()
                .filter(r -> !r.getReservationTime().isBefore(before) && !r.getReservationTime().isAfter(after))
                .mapToInt(Reservation::getNumberOfPeople)
                .sum();
        return sum + people <= store.getCapacity();
    }
}
