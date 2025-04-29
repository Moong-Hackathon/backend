package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.schedules WHERE s.storeId = :storeId")
    Optional<Store> findWithSchedulesById(@Param("storeId") Long storeId);
}