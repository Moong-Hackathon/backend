package com.hackathon.reservation.reservation_mvp.configuration;

import com.hackathon.reservation.reservation_mvp.adapter.out.SpringDataReservationAdapter;
import com.hackathon.reservation.reservation_mvp.port.out.LoadReservationPort;
import com.hackathon.reservation.reservation_mvp.port.out.SaveReservationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public LoadReservationPort loadReservationPort(SpringDataReservationAdapter adapter) {
        return adapter;
    }

    @Bean
    public SaveReservationPort saveReservationPort(SpringDataReservationAdapter adapter) {
        return adapter;
    }
}