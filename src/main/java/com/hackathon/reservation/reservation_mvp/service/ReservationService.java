package com.hackathon.reservation.reservation_mvp.service;

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

/**
 * Provides read/write operations across stores, members, and reservations,
 * and dispatches SSE notifications on status changes.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationNotificationService notificationService;

    /**
     * Returns all stores.
     */
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    /**
     * Returns only those stores where the given user has at least one reservation.
     *
     * @param userId the user’s ID
     */
    public List<Store> getStoresWithUserReservations(Long userId) {
        return storeRepository.findAll().stream()
                .filter(store -> store.getReservations().stream()
                        .anyMatch(r -> r.getMember().getMemberId().equals(userId)))
                .collect(Collectors.toList());
    }

    /**
     * Returns basic store info plus its open/close times.
     *
     * @param userId  the requesting user’s ID (reserved for future use)
     * @param storeId the store to look up
     * @throws IllegalArgumentException if no such store
     */
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

    /**
     * Submits reservation requests to all qualifying stores and returns the number of stores requested.
     *
     * @param userId the member making the reservation
     * @param dto    reservation criteria (location, time, party size)
     * @throws IllegalArgumentException if no such member
     */
    @Transactional
    public int reserveAllAvailableStores(Long userId, StoreReservationRequestDto dto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Store> candidates = storeRepository.findAll().stream()
                .filter(s -> withinDistance(dto, s))
                .filter(s -> s.getCapacity() >= dto.getNumberOfPeople())
                .filter(s -> withinHours(s, dto.getReservationTime().toLocalTime()))
                .filter(s -> slotAvailable(s, dto.getReservationTime(), dto.getNumberOfPeople()))
                .collect(Collectors.toList());

        for (Store store : candidates) {
            Reservation r = Reservation.builder()
                    .member(member)
                    .store(store)
                    .reservationTime(dto.getReservationTime())
                    .numberOfPeople(dto.getNumberOfPeople())
                    .status(ReservationStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            reservationRepository.save(r);
        }
        return candidates.size();
    }

    // --- Private helpers --------------------------------------------

    private boolean withinDistance(StoreReservationRequestDto dto, Store s) {
        double dLat = dto.getLocation().getLatitude()  - s.getLatitude();
        double dLng = dto.getLocation().getLongitude() - s.getLongitude();
        double dist = Math.hypot(dLat, dLng);
        return "NEAR".equalsIgnoreCase(dto.getDistanceType())
                ? dist < 0.0063
                : dist < 0.015;
    }

    private boolean withinHours(Store s, LocalTime t) {
        return s.getSchedules().stream().anyMatch(sc ->
                !t.isBefore(sc.getOpenTime()) && !t.isAfter(sc.getCloseTime())
        );
    }

    private boolean slotAvailable(Store s, LocalDateTime time, int people) {
        LocalDateTime from = time.minusHours(1);
        LocalDateTime to   = time.plusHours(1);
        int already = s.getReservations().stream()
                .filter(r -> !r.getReservationTime().isBefore(from)
                        && !r.getReservationTime().isAfter(to))
                .mapToInt(Reservation::getNumberOfPeople)
                .sum();
        return already + people <= s.getCapacity();
    }

    /**
     * Updates a single reservation’s status and notifies the user via SSE.
     *
     * @param reservationId the reservation to update
     * @param newStatus     the target status
     * @throws IllegalArgumentException if no such reservation
     */
    @Transactional
    public void updateReservationStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        switch (newStatus) {
            case AVAILABLE -> r.markAvailable();
            case CONFIRMED -> r.markConfirmed();
            case DENIED    -> r.markDenied();
            case CANCELED  -> r.cancel("SYSTEM");
            default        -> throw new IllegalArgumentException("Invalid status");
        }
        r.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(r);

        // send SSE
        notificationService.notifyReservationUpdate(
                r.getMember().getMemberId(), r);
    }
}