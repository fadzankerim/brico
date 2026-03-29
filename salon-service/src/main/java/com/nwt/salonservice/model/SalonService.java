package com.nwt.salonservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salon_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToMany(mappedBy = "services")
    @Builder.Default
    private List<Hairdresser> hairdressers = new ArrayList<>();
}
