package com.nwt.salonservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hairdressers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hairdresser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String specialties;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "hairdresser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkingHours> workingHours = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "hairdresser_services",
        joinColumns = @JoinColumn(name = "hairdresser_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @Builder.Default
    private List<SalonService> services = new ArrayList<>();
}
