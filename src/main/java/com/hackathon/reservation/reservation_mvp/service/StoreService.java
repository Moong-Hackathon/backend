package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.apipayload.code.status.ErrorStatus;
import com.hackathon.reservation.reservation_mvp.apipayload.exception.GeneralException;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for store-related operations: listing, checking availability,
 * creating bulk reservations, and toggling reservation open/closed state.
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    private final StoreRepository storeRepository;

    /**
     * Toggles a store’s “isReservationOpen” flag.
     *
     * @param storeId           the store to update
     * @param isReservationOpen new open/closed state
     * @return updated {@link Store}
     * @throws GeneralException if the store does not exist
     */
    @Transactional
    public Store patchStoreStatus(Long storeId, Boolean isReservationOpen) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));
        store.updateReservationStatus(isReservationOpen);
        log.info("Store {} reservationOpen set to {}", storeId, isReservationOpen);
        return store;
    }
}