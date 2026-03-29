package com.nwt.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Public (unauthenticated) routes — method + path prefix combinations.
     * Any route not listed here requires a valid JWT.
     */
    private static final List<PublicRoute> PUBLIC_ROUTES = List.of(
            new PublicRoute(HttpMethod.POST, "/api/auth/login"),
            new PublicRoute(HttpMethod.POST, "/api/auth/register"),
            new PublicRoute(HttpMethod.GET, "/api/salons"));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        HttpMethod method = request.getMethod();

        // Allow CORS preflight requests through without authentication
        if (HttpMethod.OPTIONS.equals(method)) {
            return chain.filter(exchange);
        }

        // Allow public routes through without authentication
        if (isPublicRoute(method, path)) {
            log.debug("Allowing public route: {} {}", method, path);
            return chain.filter(exchange);
        }

        // Extract the Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or malformed Authorization header for {} {}", method, path);
            return writeUnauthorizedResponse(exchange, "Missing or invalid token");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = validateToken(token);

            String userId = extractClaim(claims, "userId", "id", "sub");
            String role = extractClaim(claims, "role", "roles", "authorities");

            log.debug("JWT valid for userId={}, role={}", userId, role);

            // Mutate the request to add downstream headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId != null ? userId : "")
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (JwtException e) {
            log.warn("JWT validation failed for {} {}: {}", method, path, e.getMessage());
            return writeUnauthorizedResponse(exchange, "Missing or invalid token");
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            return writeUnauthorizedResponse(exchange, "Missing or invalid token");
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean isPublicRoute(HttpMethod method, String path) {
        return PUBLIC_ROUTES.stream().anyMatch(r -> r.matches(method, path));
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Tries multiple claim name variants (different services may use different
     * conventions).
     */
    private String extractClaim(Claims claims, String... candidateKeys) {
        for (String key : candidateKeys) {
            Object value = claims.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    private Mono<Void> writeUnauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "status", 401,
                "error", "UNAUTHORIZED",
                "message", message);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = "{\"status\":401,\"error\":\"UNAUTHORIZED\",\"message\":\"Missing or invalid token\"}"
                    .getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    // -------------------------------------------------------------------------
    // Inner record for public route definitions
    // -------------------------------------------------------------------------

    private record PublicRoute(HttpMethod method, String pathPrefix) {
        boolean matches(HttpMethod requestMethod, String requestPath) {
            return this.method.equals(requestMethod) && requestPath.startsWith(pathPrefix);
        }
    }
}
