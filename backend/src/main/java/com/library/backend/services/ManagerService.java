package com.library.backend.services;

import com.library.backend.dtos.requests.ManagerCreationRequest;
import com.library.backend.dtos.responses.ManagerDetailResponse;
import com.library.backend.dtos.responses.StudentDetailResponse;
import com.library.backend.entities.Manager;
import com.library.backend.entities.Student;
import com.library.backend.entities.User;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.mappers.ManagerMapper;
import com.library.backend.repositories.ManagerRepository;
import com.library.backend.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManagerService {

    ManagerMapper managerMapper;
    ManagerRepository managerRepository;
    UserRepository userRepository;

    public ManagerDetailResponse create(ManagerCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
        Manager manager = new Manager();
        manager.setUser(user);
        manager = managerRepository.save(manager);
        ManagerDetailResponse response = managerMapper.toManagerDetailResponse(manager);
        managerMapper.extraMap(response, manager.getUser());
        return response;
    }

    public ManagerDetailResponse getById(Integer id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.MANAGER_NOT_FOUND));
        return ManagerDetailResponse.builder()
                .id(manager.getUserId())
                .fullName(manager.getUser().getFullName())
                .email(manager.getUser().getEmail())
                .phone(manager.getUser().getPhone())
                .build();
    }

    public List<Integer> getAllIds() {
        List<Manager> managers = managerRepository.findAll();
        return managers.stream().map(Manager::getUserId).toList();
    }

    public List<ManagerDetailResponse> getAll() {
        List<Manager> managers = managerRepository.findAll();
        return managers.stream().map(
                manager -> ManagerDetailResponse.builder()
                        .id(manager.getUserId())
                        .fullName(manager.getUser().getFullName())
                        .email(manager.getUser().getEmail())
                        .phone(manager.getUser().getPhone())
                        .build()
        ).toList();
    }

    public boolean existsById(Integer id) {
        return managerRepository.existsById(id);
    }

    public ManagerDetailResponse getByUsernameAndPassword(String username, String password) {
        Manager manager = managerRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new GeneralException(ResponseCode.MANAGER_NOT_FOUND));
        return ManagerDetailResponse.builder()
                .id(manager.getUserId())
                .fullName(manager.getUser().getFullName())
                .email(manager.getUser().getEmail())
                .phone(manager.getUser().getPhone())
                .build();
    }

    public boolean isInit() {
        return managerRepository.count() > 0;
    }


}
