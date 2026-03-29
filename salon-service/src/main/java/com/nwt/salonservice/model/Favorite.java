package com.nwt.salonservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "salon_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id")
    private Salon salon;
}
