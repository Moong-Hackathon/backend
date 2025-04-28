package com.hackathon.reservation.reservation_mvp.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    private String storeName;
    private Double latitude;
    private Double longitude;
    private Integer capacity;

    private String address;  // 주소 추가
    private String mainImage; // 메인 이미지 파일명
    @ElementCollection
    @CollectionTable(
            name = "store_menu_images", // 테이블 이름 명시
            joinColumns = @JoinColumn(name = "store_id") // 매핑할 FK 명시
    )
    @Column(name = "menu_image") // 실제 저장될 값 (컬럼명 지정)
    private List<String> menuImages;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

}
