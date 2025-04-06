package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.dto.ReservationEvent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/v1/users/reservations")
public class ReservationEventController {

    // 사용자 ID에 따른 SseEmitter를 관리합니다.
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long userId) {
        // 30분(30*60*1000ms) 타임아웃 설정
        SseEmitter emitter = new SseEmitter(30L * 60 * 1000);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        return emitter;
    }

    /**
     * 특정 사용자에게 이벤트 전송
     * @param userId 대상 사용자 ID
     * @param event 전송할 이벤트 데이터
     */
    public void sendEvent(Long userId, ReservationEvent event) {
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