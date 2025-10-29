package com.library.backend.services;

import com.library.backend.dtos.requests.UserCreationRequest;
import com.library.backend.dtos.requests.UserUpdateRequest;
import com.library.backend.dtos.requests.V1PasswordRequest;
import com.library.backend.dtos.responses.UserDetailResponse;
import com.library.backend.dtos.responses.V1AccountResponse;
import com.library.backend.entities.User;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.mappers.UserMapper;
import com.library.backend.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('Manager')")
    public List<UserDetailResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDetailResponse).toList();
    }

    public List<UserDetailResponse> init_getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDetailResponse).toList();
    }

    public UserDetailResponse create(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        return userMapper.toUserDetailResponse(user);
    }

    public UserDetailResponse init_create(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        return userMapper.toUserDetailResponse(user);
    }

    @PreAuthorize("#userId == authentication.principal")
    public UserDetailResponse update(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
        userMapper.update(user, request);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user = userRepository.save(user);
        return userMapper.toUserDetailResponse(user);
    }

    public UserDetailResponse insert_update(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
        userMapper.update(user, request);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user = userRepository.save(user);
        return userMapper.toUserDetailResponse(user);
    }

    public UserDetailResponse getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
        return userMapper.toUserDetailResponse(user);
    }

    @PreAuthorize("hasRole('Manager')")
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public UserDetailResponse getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
        return userMapper.toUserDetailResponse(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isInit() {
        long count = userRepository.count();
        return count > 0;
    }

}
