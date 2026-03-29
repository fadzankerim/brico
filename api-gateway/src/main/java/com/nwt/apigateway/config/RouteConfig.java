package com.nwt.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ── Auth & Users ──────────────────────────────────────────
                .route("user-service-auth", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://user-service"))

                .route("user-service-users", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://user-service"))

                // ── Salon Service ─────────────────────────────────────────
                .route("salon-service-salons", r -> r
                        .path("/api/salons/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://salon-service"))

                .route("salon-service-hairdressers", r -> r
                        .path("/api/hairdressers/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://salon-service"))

                .route("salon-service-services", r -> r
                        .path("/api/services/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://salon-service"))

                .route("salon-service-working-hours", r -> r
                        .path("/api/working-hours/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://salon-service"))

                .route("salon-service-favorites", r -> r
                        .path("/api/favorites/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://salon-service"))

                // ── Appointment Service ───────────────────────────────────
                .route("appointment-service-appointments", r -> r
                        .path("/api/appointments/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://appointment-service"))

                .route("appointment-service-reviews", r -> r
                        .path("/api/reviews/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://appointment-service"))

                // ── Notification Service ──────────────────────────────────
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://notification-service"))

                // ── System Events Service ─────────────────────────────────
                .route("system-events-service", r -> r
                        .path("/api/events/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("lb://system-events-service"))

                .build();
    }
}
