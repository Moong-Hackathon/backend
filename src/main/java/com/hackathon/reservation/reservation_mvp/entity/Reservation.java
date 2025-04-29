package com.hackathon.reservation.reservation_mvp.entity;


import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    private LocalDateTime reservationTime;

    private Integer numberOfPeople;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String canceledBy;  // "MEMBER" 또는 "STORE"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ====== 비즈니스 메서드 ======
    public void available() {this.status = ReservationStatus.AVAILABLE;}

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void deny() {
        this.status = ReservationStatus.DENIED;
    }

    public void cancel(String by) {
        this.status = ReservationStatus.CANCELED;
        this.canceledBy = by; //STORE 또는 MEMBER
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
