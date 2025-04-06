package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.service.ReservationNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationSseController.class)
public class ReservationSseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationNotificationService notificationService;

    @Test
    @WithMockUser
    public void testSubscribe_ReturnsSseEmitter() throws Exception {
        // 테스트용으로 짧은 타임아웃(1000L)으로 SseEmitter 생성
        SseEmitter emitter = new SseEmitter(1000L);
        when(notificationService.subscribeUser(anyLong())).thenReturn(emitter);

        mockMvc.perform(get("/v1/users/reservations/events")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }
}