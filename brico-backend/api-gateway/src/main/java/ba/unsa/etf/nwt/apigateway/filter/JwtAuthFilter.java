package ba.unsa.etf.nwt.apigateway.filter;

import ba.unsa.etf.nwt.apigateway.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private record PublicEndpoint(String method, String path) {}

    // Endpointi koji ne zahtijevaju JWT
    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = List.of(
            new PublicEndpoint("POST", "/api/auth/login"),
            new PublicEndpoint("POST", "/api/auth/refresh"),
            new PublicEndpoint("POST", "/api/users")   // registracija
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path    = request.getURI().getPath();
        String method  = request.getMethod().name();

        if (isPublic(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return respondUnauthorized(exchange, "Nedostaje Authorization header (Bearer token)");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isValid(token)) {
            return respondUnauthorized(exchange, "Token nije validan ili je istekao");
        }

        Claims claims = jwtUtil.extractClaims(token);

        // Prosljeđuj user info prema mikroservisima putem headera
        ServerWebExchange mutated = exchange.mutate()
                .request(request.mutate()
                        .header("X-User-Id",    claims.getSubject())
                        .header("X-User-Role",  claims.get("role",  String.class))
                        .header("X-User-Email", claims.get("email", String.class))
                        .build())
                .build();

        log.debug("JWT validan — userId={} role={} path={}", claims.getSubject(), claims.get("role"), path);
        return chain.filter(mutated);
    }

    private boolean isPublic(String path, String method) {
        // Svi GET zahtjevi prema salonima su javni (pretraga bez prijave)
        if ("GET".equals(method) && path.startsWith("/api/salons")) return true;
        // Actuator endpointi
        if (path.startsWith("/actuator")) return true;

        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(e -> e.method().equals(method) && path.equals(e.path()));
    }

    private Mono<Void> respondUnauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "error",     "unauthorized",
                "message",   message,
                "timestamp", LocalDateTime.now().toString()
        );
        try {
            byte[] bytes  = objectMapper.writeValueAsBytes(body);
            DataBuffer buf = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buf));
        } catch (JsonProcessingException e) {
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // izvršava se prvi
    }
}
