package com.billings.middlewareservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String jwtSecret;

    // 2. Added a manual constructor with @Value injection
    public JwtAuthenticationFilter(@Value("${jwt.secret.key}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api/v1/public/");
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,@NotNull HttpServletResponse response,@NotNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Parse and Validate Token
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userUuid = claims.getSubject(); // User UUID from Auth Service

            // Extract Roles and Permissions from Claims
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get("permissions", List.class);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if (roles != null) {
                roles.stream()
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }

            if (permissions != null) {
                permissions.stream()
                        .map(SimpleGrantedAuthority::new) // No prefix for permissions
                        .forEach(authorities::add);
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userUuid, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Token validation failed (expired, tampered, etc.)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
