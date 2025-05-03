package com.hackathon.reservation.reservation_mvp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A user of the system who can make reservations.
 */
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

        /** Primary key. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long memberId;

        /** The member’s display name. */
        private String name;

        /** All reservations made by this member. */
        @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<Reservation> reservations = new ArrayList<>();
}