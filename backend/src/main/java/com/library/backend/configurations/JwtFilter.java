package com.library.backend.configurations;

import com.library.backend.utils.JwtUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtFilter extends OncePerRequestFilter {

    JwtUtil jwtUtil;

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            for (String endpoint : SecurityConfig.PUBLIC_GET_ENDPOINTS) {
                String regex = endpoint.replace("/**", "(/.*)?");
                if (Pattern.matches(regex, path)) return true;
            }
        }

        if ("POST".equalsIgnoreCase(method)) {
            for (String endpoint : SecurityConfig.PUBLIC_POST_ENDPOINTS) {
                String regex = endpoint.replace("/**", "(/.*)?");
                if (Pattern.matches(regex, path)) return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.verifyToken(token)) {
                JWTClaimsSet jwtClaimsSet = jwtUtil.getClaimSetFromToken(token);
                Long userId = null;
                try {
                    userId = Long.valueOf(jwtClaimsSet.getSubject());
                }
                catch (NumberFormatException exception) {
                    throw new BadCredentialsException("Invalid JWT token");
                }
                String scope = jwtClaimsSet.getClaim("scope").toString();
                List<String> roles = List.of(scope.replace("ROLE_", "").split(" "));
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            else {
                throw new BadCredentialsException("Invalid JWT token");
            }
        }
        else {
            throw new BadCredentialsException("Missing JWT token");
        }
        filterChain.doFilter(request, response);
    }
}