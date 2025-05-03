package com.hackathon.reservation.reservation_mvp.service;

import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Server-Sent Events (SSE) subscriptions and notifications
 * for reservation updates.
 */
@Slf4j
@Service
public class ReservationNotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Subscribes the given user to SSE events.
     *
     * @param userId the user’s ID
     * @return a new {@link SseEmitter} tied to that user
     */
    public SseEmitter subscribeUser(Long userId) {
        // 30-minute timeout
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(()    -> emitters.remove(userId));
        emitter.onError(e       -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("subscription")
                    .data("Subscribed successfully.")
                    .reconnectTime(3_000));
        } catch (IOException e) {
            log.warn("Failed to send subscription confirmation to userId={}", userId, e);
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * Sends an SSE event to the given user about a reservation update.
     *
     * @param userId      the user’s ID
     * @param reservation the updated reservation
     */
    public void notifyReservationUpdate(Long userId, Reservation reservation) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.debug("No SSE emitter found for userId={}", userId);
            return;
        }

        try {
            log.info("Sending SSE to userId={} reservationId={} status={}",
                    userId, reservation.getReservationId(), reservation.getStatus());

            emitter.send(SseEmitter.event()
                    .name("reservationUpdate")
                    .data(Map.of(
                            "reservationId", reservation.getReservationId(),
                            "status",        reservation.getStatus().name(),
                            "updatedAt",     reservation.getUpdatedAt()
                    )));
        } catch (IOException e) {
            log.error("Error sending SSE to userId={}", userId, e);
            emitter.completeWithError(e);
            emitters.remove(userId);
        }
    }
}