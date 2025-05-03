package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for {@link Store} entities.
 *
 * <p>Includes a custom fetch-join to load schedules eagerly.
 */
public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * Fetches a single store along with its operating schedules.
     *
     * @param storeId the store identifier
     * @return the store with schedules loaded, if found
     */
    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.schedules WHERE s.storeId = :storeId")
    Optional<Store> findWithSchedulesById(@Param("storeId") Long storeId);
}