package com.library.backend.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.services.ThrottlingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThrottlingFilter extends OncePerRequestFilter {

    ThrottlingService throttlingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userKey = extractUserKey();
        if (userKey == null) {
            userKey = "ip:" + request.getRemoteAddr();
        }

        try {
            throttlingService.throttle(userKey);
            filterChain.doFilter(request, response);
        }
        catch (GeneralException e) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getCode().getMessage())
                    .build();
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(apiResponse));
            response.flushBuffer();
            return;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String extractUserKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Long) {
                return principal.toString();
            }
            else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }


}
