package com.nwt.appointmentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "hairdresser_id")
    private Long hairdresserId;

    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
