package com.library.backend.services;

import com.library.backend.dtos.responses.AuthResponse;
import com.library.backend.entities.User;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.repositories.ManagerRepository;
import com.library.backend.repositories.StudentRepository;
import com.library.backend.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    StudentRepository studentRepository;
    ManagerRepository managerRepository;

    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new GeneralException(ResponseCode.UNAUTHENTICATE));
        boolean isManager = managerRepository.existsById(user.getId());
        if (isManager) {
            return AuthResponse.builder().accountType("Manager").build();
        }
        return AuthResponse.builder().accountType("User").build();
    }

}
