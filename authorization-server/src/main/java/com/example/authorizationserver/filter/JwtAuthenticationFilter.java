package com.example.authorizationserver.filter;

import com.example.authorizationserver.domain.constant.Code;
import com.example.authorizationserver.domain.response.Exception.CommonException;
import com.example.authorizationserver.util.KeyUtils;
import com.example.authorizationserver.service.UserManager;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.authorizationserver.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final KeyUtils keyUtils;
    private final UserManager userManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);
        if (token == null) {
            log.warn("No token provided in the request.");

            filterChain.doFilter(request, response);
            return;
        }

        try {
            log.info("Parsing token: {}", token);
            Claims claims = parseToken(token);
            authenticateUser(claims);
            log.info("User authenticated successfully.");
        } catch (Exception e) {
            log.error("Token parsing or authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            log.debug("Extracted token from Authorization header.");
            return header.substring(7);
        }
        log.warn("No Bearer token found in Authorization header.");
        return null;
    }

    private Claims parseToken(String token) {
        try {
            Key secretKey = keyUtils.getSecretKey();
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            log.debug("Token parsed successfully.");
            return claimsJws.getBody();
        } catch (JwtException e) {
            log.error("Token parsing failed: {}", e.getMessage());
            throw new JwtException("Invalid token");
        }
    }

    private void authenticateUser(Claims claims) {
        String userId = claims.getSubject();
        log.info("Authenticating user with ID: {}", userId);

        User user = (User) userManager.loadUserById(Long.parseLong(userId));
        if (user == null) {
            log.warn("User not found: {}", userId);
            return;
        }

        List<String> roles = claims.get("roles", List.class);
        List<SimpleGrantedAuthority> authorities = Optional.ofNullable(roles)
                .orElse(Collections.emptyList())
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, authorities)
        );
        log.info("User authenticated and roles assigned.");
    }
}
