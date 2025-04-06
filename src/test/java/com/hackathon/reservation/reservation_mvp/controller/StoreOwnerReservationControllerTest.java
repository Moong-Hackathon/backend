package com.hackathon.reservation.reservation_mvp.controller;

import com.hackathon.reservation.reservation_mvp.entity.Member;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.service.reservation.ReservationQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreOwnerReservationController.class)
public class StoreOwnerReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationQueryService reservationQueryService;

    @Test
    @WithMockUser
    public void testGetReservations_ReturnsReservationList() throws Exception {
        // 더미 Member와 Reservation 생성
        Member dummyMember = Member.builder()
                .memberId(1L)
                .build();
        Reservation dummyReservation = Reservation.builder()
                .reservationId(1L)
                .member(dummyMember)
                // 나머지 필드(예: store, reservationTime 등)는 필요한 경우 추가
                .build();

        Page<Reservation> page = new PageImpl<>(Collections.singletonList(dummyReservation),
                PageRequest.of(0, 10), 1);
        when(reservationQueryService.getReservations(eq(100L), eq(0))).thenReturn(page);

        mockMvc.perform(get("/v1/stores/100/reservations")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        // jsonPath() 등을 활용해 응답 본문의 세부 항목을 추가 검증할 수 있습니다.
    }
}