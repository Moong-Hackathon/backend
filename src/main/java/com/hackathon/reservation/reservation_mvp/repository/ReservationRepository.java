package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByStore_StoreId(Long storeId, Pageable pageable);
    Optional<Reservation> findByReservationIdAndStore_StoreId(Long reservationId, Long storeId);
    Optional<Reservation> findByReservationIdAndMember_MemberId(Long reservationId, Long memberId);
}

