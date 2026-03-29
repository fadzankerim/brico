package com.nwt.salonservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String city;

    private String address;

    private Double lat;

    private Double lng;

    @Column(name = "avg_rating")
    @Builder.Default
    private Double avgRating = 0.0;

    @Builder.Default
    private Boolean verified = false;

    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Hairdresser> hairdressers = new ArrayList<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SalonService> services = new ArrayList<>();
}
