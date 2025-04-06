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
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long userId;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private List<Reservation> reservations = new ArrayList<>();

}
