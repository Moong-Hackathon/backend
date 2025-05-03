package com.hackathon.reservation.reservation_mvp.service.reservation;

import com.hackathon.reservation.reservation_mvp.apipayload.code.status.ErrorStatus;
import com.hackathon.reservation.reservation_mvp.apipayload.exception.GeneralException;
import com.hackathon.reservation.reservation_mvp.dto.StoreReservationRequestDto;
import com.hackathon.reservation.reservation_mvp.entity.Member;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ReservationCommandService} 구현체.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationNotificationService notificationService;

    @Override
    public int reserveAllAvailableStores(Long userId, StoreReservationRequestDto dto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESERVATION_MEMBER_MISMATCH));

        List<Store> candidates = storeRepository.findAll().stream()
                .filter(s -> withinDistance(dto, s))
                .filter(s -> s.getCapacity() >= dto.getNumberOfPeople())
                .filter(s -> withinOperatingHours(s, dto.getReservationTime().toLocalTime()))
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

    @Override
    public Reservation updateReservationStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESERVATION_NOT_FOUND));

        switch (newStatus) {
            case AVAILABLE  -> r.markAvailable();
            case CONFIRMED  -> r.markConfirmed();
            case DENIED     -> r.markDenied();
            case CANCELED   -> r.cancel("SYSTEM");
            default         -> throw new GeneralException(ErrorStatus.INVALID_RESERVATION_STATUS);
        }
        r.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(r);

        // Notification은 오직 여기서만
        notificationService.notifyReservationUpdate(
                r.getMember().getMemberId(), r);

        return r;
    }

    /* --- private helper methods --- */

    private boolean withinDistance(StoreReservationRequestDto dto, Store s) {
        double dLat = dto.getLocation().getLatitude()  - s.getLatitude();
        double dLng = dto.getLocation().getLongitude() - s.getLongitude();
        double dist = Math.hypot(dLat, dLng);
        return "NEAR".equalsIgnoreCase(dto.getDistanceType())
                ? dist < 0.0063
                : dist < 0.015;
    }

    private boolean withinOperatingHours(Store s, LocalTime t) {
        return s.getSchedules().stream()
                .anyMatch(sc -> !t.isBefore(sc.getOpenTime()) && !t.isAfter(sc.getCloseTime()));
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
}