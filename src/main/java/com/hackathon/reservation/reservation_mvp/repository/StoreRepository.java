package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("SELECT DISTINCT s " +
            "FROM Store s " +
            "JOIN s.reservations r " +
            "WHERE r.member.memberId = :memberId")
    List<Store> findAllByMemberId(@Param("memberId") Long memberId);
}
