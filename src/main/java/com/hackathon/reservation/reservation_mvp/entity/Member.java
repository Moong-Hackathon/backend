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
public class Member {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long memberId;

        @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
        private List<Reservation> reservations = new ArrayList<>();

}
