package com.cineverse.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final SecretKey key;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Bypass public authentication endpoints
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }

        // Get Authorization Header
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            return onError(exchange, "Unauthorized: Missing authorization header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = authHeaders.get(0);
        if (!authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Unauthorized: Invalid authorization format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                return onError(exchange, "Unauthorized: Token is expired", HttpStatus.UNAUTHORIZED);
            }

            // Extract claims
            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            // Mutate request headers to forward user details downstream
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            return onError(exchange, "Unauthorized: Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"status\":\"error\",\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // Run first in the filter chain
    }
}
