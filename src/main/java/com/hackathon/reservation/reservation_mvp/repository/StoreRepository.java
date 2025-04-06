package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
