package com.library.backend.services;

import com.library.backend.dtos.requests.AuthRequest;
import com.library.backend.dtos.responses.AuthResponse;
import com.library.backend.entities.RefreshToken;
import com.library.backend.entities.User;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.repositories.ManagerRepository;
import com.library.backend.repositories.RefreshTokenRepository;
import com.library.backend.repositories.StudentRepository;
import com.library.backend.repositories.UserRepository;
import com.library.backend.utils.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    StudentRepository studentRepository;
    ManagerRepository managerRepository;
    PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;
    RefreshTokenRepository refreshTokenRepository;
    @NonFinal
    @Value("${jwt.refreshToken.expirationTime}")
    int refreshTokenExpirationTime;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new GeneralException(ResponseCode.UNAUTHENTICATE));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new GeneralException(ResponseCode.UNAUTHENTICATE);
        }
        boolean isManager = managerRepository.existsById(user.getId());
        String role = isManager ? "Manager" : "User";
        String accessToken = jwtUtil.generateAccessToken(user, role);
        RefreshToken refreshToken = jwtUtil.generateRefreshToken(user, "Unknown");
        Optional<RefreshToken> existToken = refreshTokenRepository.findByUserIdAndDeviceInfo(user.getId(), refreshToken.getDeviceInfo());
        existToken.ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.save(refreshToken);
        return AuthResponse.builder()
                .accountType(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken.getId())
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new GeneralException(ResponseCode.UNAUTHENTICATE));
        if (token.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new GeneralException(ResponseCode.UNAUTHENTICATE);
        }
        token.setExpirationTime(LocalDateTime.ofInstant(
                Instant.now().plusSeconds(refreshTokenExpirationTime),
                ZoneId.systemDefault()
        ));
        refreshTokenRepository.save(token);
        boolean isManager = managerRepository.existsById(token.getUser().getId());
        String role = isManager ? "Manager" : "User";
        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(token.getUser(), role))
                .refreshToken(refreshToken)
                .accountType(role)
                .build();
    }

    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new GeneralException(ResponseCode.UNAUTHENTICATE));
        if (token.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new GeneralException(ResponseCode.UNAUTHENTICATE);
        }
        refreshTokenRepository.deleteById(token.getId());
    }

}
