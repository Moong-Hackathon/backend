package com.hackathon.reservation.reservation_mvp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.reservation.reservation_mvp.entity.Member;
import com.hackathon.reservation.reservation_mvp.entity.Reservation;
import com.hackathon.reservation.reservation_mvp.entity.Store;
import com.hackathon.reservation.reservation_mvp.entity.StoreSchedule;
import com.hackathon.reservation.reservation_mvp.entity.enums.DayOfWeek;
import com.hackathon.reservation.reservation_mvp.entity.enums.ReservationStatus;
import com.hackathon.reservation.reservation_mvp.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StoreControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserReservationController storeController;

    @Mock
    private StoreService storeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(storeController).build();
    }

    @Test
    @DisplayName("예약 목록 조회 API 테스트")
    public void testGetStoresWithReservations() throws Exception {
        // 더미 Member, Reservation, StoreSchedule, Store 객체 생성
        Member member = Member.builder()
                .memberId(1L)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId(100L)
                .member(member)
                .reservationTime(LocalDateTime.of(2025, 4, 15, 18, 0))
                .numberOfPeople(4)
                .status(ReservationStatus.AVAILABLE)
                .canceledBy(null)
                .build();

        StoreSchedule schedule = StoreSchedule.builder()
                .id(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(21, 0))
                .build();

        Store store = Store.builder()
                .storeId(10L)
                .storeName("Test Store")
                .latitude(37.12345)
                .longitude(127.12345)
                .capacity(50)
                .reservations(new ArrayList<>())
                .schedules(new ArrayList<>())
                .build();

        // 수동으로 관계 설정 (양쪽 컬렉션에 추가)
        store.getReservations().add(reservation);
        store.getSchedules().add(schedule);

        // Service가 해당 사용자의 예약 목록을 반환하도록 모킹
        when(storeService.getStoresWithUserReservations(1L))
                .thenReturn(List.of(store));

        // GET 요청으로 API 호출 후 응답에 필요한 정보들이 포함되었는지 검증
        mockMvc.perform(get("/v1/users/1/reservations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test Store")))
                .andExpect(content().string(containsString("AVAILABLE")))
                .andExpect(content().string(containsString("9:00")))
                .andExpect(content().string(containsString("21:00")));
    }
}