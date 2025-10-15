package com.library.backend.controllers.v1;

import com.library.backend.dtos.requests.ManagerCreationRequest;
import com.library.backend.dtos.requests.UserUpdateRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.ManagerDetailResponse;
import com.library.backend.dtos.responses.UserDetailResponse;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.services.ManagerService;
import com.library.backend.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/managers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1ManagerController {

    ManagerService managerService;
    UserService userService;

    @GetMapping
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        List<ManagerDetailResponse> list = managerService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(
                        response -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("accountID", response.getId());
                            map.put("fullName", response.getFullName());
                            map.put("email", response.getEmail());
                            map.put("phone", response.getPhone());
                            return map;
                        }
                ).toList()
        ));
    }

    @PostMapping
    ResponseEntity<ApiResponse<Void>> insert(
            @RequestBody Map<String, Object> request
    ) {
        Integer userId = (Integer) request.get("accountID");
        String fullName = (String) request.get("fullName");
        String email = (String) request.get("email");
        String phone = (String) request.get("phone");
        boolean isManager = managerService.existsById(userId);
        if (!isManager) {
            throw new GeneralException(ResponseCode.MANAGER_NOT_FOUND);
        }
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .build();
        userService.update(userId, userUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        String fullName = (String) request.get("fullName");
        String email = (String) request.get("email");
        String phone = (String) request.get("phone");
        boolean isManager = managerService.existsById(id);
        if (!isManager) {
            throw new GeneralException(ResponseCode.MANAGER_NOT_FOUND);
        }
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .build();
        userService.update(id, userUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {
        userService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<Map<String, Object>>> get(
            @PathVariable Integer id
    ) {
        ManagerDetailResponse response = managerService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("accountID", response.getId());
        map.put("fullName", response.getFullName());
        map.put("email", response.getEmail());
        map.put("phone", response.getPhone());
        return ResponseEntity.ok().body(ApiResponse.success(map));
    }

    @GetMapping("/ids")
    ResponseEntity<ApiResponse<List<Integer>>> getAllID() {
        List<Integer> ids = managerService.getAllIds();
        return ResponseEntity.ok().body(ApiResponse.success(ids));
    }

}
