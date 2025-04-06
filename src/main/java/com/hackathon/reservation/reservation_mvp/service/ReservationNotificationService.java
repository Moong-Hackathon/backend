package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.dto.ReservationEvent;
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
        SseEmitter emitter = new SseEmitter(30L * 60 * 1000); // 30분 타임아웃
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        return emitter;
    }

    /**
     * 특정 사용자에게 SSE 이벤트를 전송합니다.
     * @param userId 대상 사용자 ID
     * @param event 전송할 이벤트 데이터
     */
    public void notifyReservationUpdate(Long userId, ReservationEvent event) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("reservationUpdate")
                        .data(event)
                        .reconnectTime(3000));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(userId);
            }
        }
    }
}