package com.library.backend.controllers;

import com.library.backend.dtos.requests.AuthRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.AuthResponse;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.services.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody AuthRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, Object> request
    ) {
        String refreshToken = (String) request.get("refreshToken");
        if (refreshToken == null) {
            throw new GeneralException(ResponseCode.UNAUTHENTICATE);
        }
        AuthResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody Map<String, Object> request
    ) {
        String refreshToken = (String) request.get("refreshToken");
        if (refreshToken == null) {
            throw new GeneralException(ResponseCode.UNAUTHENTICATE);
        }
        authService.logout(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
