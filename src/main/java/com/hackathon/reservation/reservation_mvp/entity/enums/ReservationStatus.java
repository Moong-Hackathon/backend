package com.hackathon.reservation.reservation_mvp.entity.enums;

public enum ReservationStatus {
    PENDING, //처음에 사용자가 보냈을 때
    AVAILABLE, //가게가 ok 했을 때
    CONFIRMED, //사용자가 최종 ok 했을때
    DENIED, //가게가 안된다고 거절(처음에)
    CANCELED //사용자/가게가 취소했을때
}
