package com.hackathon.reservation.reservation_mvp.configuration;

import com.hackathon.reservation.reservation_mvp.repository.ReservationRepository;
import com.hackathon.reservation.reservation_mvp.repository.StoreRepository;
import com.hackathon.reservation.reservation_mvp.service.ReservationNotificationService;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationCommandService;
import com.hackathon.reservation.reservation_mvp.service.reservation.impl.ReservationCommandServiceImpl;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationQueryService;
import com.hackathon.reservation.reservation_mvp.service.reservation.impl.ReservationQueryServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ReservationCommandService reservationCommandService(
            ReservationRepository reservationRepository,
            ReservationNotificationService notificationService
    ) {
        return new ReservationCommandServiceImpl(reservationRepository, notificationService);
    }

    @Bean
    public ReservationQueryService reservationQueryService(
            ReservationRepository reservationRepository,
            StoreRepository storeRepository
    ) {
        return new ReservationQueryServiceImpl(reservationRepository, storeRepository);
    }
}