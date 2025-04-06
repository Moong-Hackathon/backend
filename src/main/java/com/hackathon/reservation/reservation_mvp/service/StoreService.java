package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<Store> getStoresWithUserReservations(Long userId) {
        return storeRepository.findAllByMemberId(userId);
    }
}
