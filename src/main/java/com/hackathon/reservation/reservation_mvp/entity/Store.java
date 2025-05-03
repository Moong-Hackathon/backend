package com.hackathon.reservation.reservation_mvp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A merchant location that accepts reservations.
 */
@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store {

    /** Primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    /** Human‐readable name. */
    private String storeName;

    /** GPS latitude. */
    private Double latitude;

    /** GPS longitude. */
    private Double longitude;

    /** Maximum capacity per time slot. */
    private Integer capacity;

    /** Physical address. */
    private String address;

    /** Filename or URL of primary image. */
    private String mainImage;

    /** Filenames or URLs of menu images. */
    @ElementCollection
    @CollectionTable(name = "store_menu_images",
            joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "menu_image")
    @Builder.Default
    private List<String> menuImages = new ArrayList<>();

    /** Operating schedules by day of week. */
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StoreSchedule> schedules = new ArrayList<>();

    /** All reservations booked for this store. */
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    /** Whether this store is currently accepting new reservations. */
    @Builder.Default
    private Boolean isReservationOpen = true;

    /**
     * Toggles whether new reservations are accepted.
     *
     * @param open true to open, false to close
     */
    public void updateReservationStatus(Boolean open) {
        this.isReservationOpen = open;
    }
}