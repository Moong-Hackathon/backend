package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReservationNotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 사용자를 SSE 이벤트에 구독시킵니다.
     * @param userId 대상 사용자 ID
     * @return 생성된 SseEmitter
     */
    public SseEmitter subscribeUser(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        // (선택) 연결 직후 테스트용 dummy 데이터 보내기
        try {
            emitter.send(SseEmitter.event()
                    .name("subscription")
                    .data("Subscribed successfully.")
                    .reconnectTime(3000));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public void notifyReservationUpdate(Long userId, Reservation reservation) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                // ✅ 여기 로그 추가
                System.out.println("[ReservationNotificationService] Sending SSE event to userId=" + userId + " with reservationId=" + reservation.getReservationId() + ", status=" + reservation.getStatus());

                emitter.send(Map.of(
                        "reservationId", reservation.getReservationId(),
                        "status", reservation.getStatus().name(),
                        "updatedAt", reservation.getUpdatedAt()
                ));
            } catch (IOException e) {
                System.out.println("[ReservationNotificationService] IOException while sending event to userId=" + userId);
                emitter.completeWithError(e);
                emitters.remove(userId);
            }
        } else {
            // ✅ emitter가 아예 없는 경우 로그
            System.out.println("[ReservationNotificationService] No emitter found for userId=" + userId);
        }
    }
}