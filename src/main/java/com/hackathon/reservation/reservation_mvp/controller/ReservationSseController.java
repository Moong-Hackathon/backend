package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.service.ReservationNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST controller for Server-Sent Events on reservation updates.
 */
@RestController
@RequestMapping("/v1/users/reservations")
@RequiredArgsConstructor
public class ReservationSseController {

    private final ReservationNotificationService notificationService;

    /**
     * Subscribes a user to reservation status change events via SSE.
     *
     * @param userId the ID of the user subscribing
     * @return an {@link SseEmitter} for streaming reservation updates
     */
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "SSE subscription for reservation updates",
            description = "Allows a user to subscribe to reservation status change events using Server-Sent Events."
    )
    public SseEmitter subscribeToReservationEvents(@RequestParam Long userId) {
        return notificationService.subscribeUser(userId);
    }
}