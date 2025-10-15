package com.library.backend.controllers;

import com.library.backend.dtos.requests.AuthRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.AuthResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> auth(
            @RequestBody AuthRequest request
    ) {
        return null;
    }

}
