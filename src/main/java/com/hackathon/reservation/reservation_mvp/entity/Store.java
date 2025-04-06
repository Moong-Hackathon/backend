package com.hackathon.reservation.reservation_mvp.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    private String storeName;
    private Double latitude;
    private Double longitude;
    private Integer capacity;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
}
