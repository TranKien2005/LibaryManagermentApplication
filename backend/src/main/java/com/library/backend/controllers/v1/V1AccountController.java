package com.library.backend.controllers.v1;

import com.library.backend.dtos.requests.*;
import com.library.backend.dtos.responses.*;
import com.library.backend.entities.Student;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.services.ManagerService;
import com.library.backend.services.StudentService;
import com.library.backend.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1AccountController {

    UserService userService;
    ManagerService managerService;
    StudentService studentService;

    @GetMapping
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        List<StudentDetailResponse> students = studentService.getAll();
        List<ManagerDetailResponse> managers = managerService.getAll();
        List<Map<String, Object>> list = new ArrayList<>();
        for (StudentDetailResponse student:students) {
            Map<String, Object> res = new HashMap<>();
            res.put("accountID", student.getId());
            res.put("username", "****");
            res.put("password", "****");
            res.put("accountType", "User");
            list.add(res);
        }
        for (ManagerDetailResponse manager:managers) {
            Map<String, Object> res = new HashMap<>();
            res.put("accountID", manager.getId());
            res.put("username", "****");
            res.put("password", "****");
            res.put("accountType", "Manager");
            list.add(res);
        }
        return ResponseEntity.ok().body(ApiResponse.success(list));
    }

    @PostMapping
    ResponseEntity<ApiResponse<Void>> insert (
            @RequestBody Map<String, Object> request
    ) {
        String type = (String) request.get("accountType");
        if (!type.equals("User") && !type.equals("Manager")) {
            throw new GeneralException(ResponseCode.UNKNOWN_ERROR);
        }
        UserDetailResponse userDetailResponse = userService.create(
                UserCreationRequest.builder()
                        .username((String) request.get("username"))
                        .password((String) request.get("password"))
                        .build()
        );
        if (type.equals("User")) {
            studentService.create(
                    StudentCreationRequest.builder()
                            .userId(userDetailResponse.getId())
                            .build()
            );
        }
        else {
            managerService.create(
                    ManagerCreationRequest.builder()
                            .userId(userDetailResponse.getId())
                            .build()
            );
        }
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .password((String) request.get("password"))
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
    ResponseEntity<ApiResponse<Map<String, Object>>> getID(
            @PathVariable Integer id
    ) {
        UserDetailResponse response = userService.getById(id);
        Map<String, Object> res = new HashMap<>();
        boolean isManager = managerService.existsById(response.getId());
        String type = "User";
        if (isManager) {
            type = "Manager";
        }
        res.put("accountID", response.getId());
        res.put("username", "****");
        res.put("password", "****");
        res.put("accountType", type);
        return ResponseEntity.ok().body(ApiResponse.success(res));
    }

    @GetMapping("/by-username")
    ResponseEntity<ApiResponse<Map<String, Object>>> getByUsername(
            @RequestParam String username
    ) {
        UserDetailResponse userDetailResponse = userService.getByUsername(username);
        String type = "User";
        try {
            ManagerDetailResponse managerDetailResponse = managerService.getById(userDetailResponse.getId());
            type = "Manager";
        }
        catch (GeneralException ignored) {}
        Map<String, Object> res = new HashMap<>();
        res.put("accountID", userDetailResponse.getId());
        res.put("username", "****");
        res.put("password", "****");
        res.put("accountType", type);
        return ResponseEntity.ok().body(
                ApiResponse.success(res)
        );
    }

    @PutMapping("/{id}/password")
    ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        String password = (String) request.get("password");
        UserDetailResponse userDetailResponse = userService.update(
                id,
                UserUpdateRequest.builder().password(password).build()
        );
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/get-id")
    ResponseEntity<ApiResponse<Integer>> create(
            @RequestBody Map<String, Object> request
    ) {
        String type = (String) request.get("accountType");
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        if (!type.equals("User") && !type.equals("Manager")) {
            throw new GeneralException(ResponseCode.UNKNOWN_ERROR);
        }
        if (type.equals("User")) {
            StudentDetailResponse student = studentService.getByUsernameAndPassword(username, password);
            return ResponseEntity.ok().body(ApiResponse.success(student.getId()));
        }
        else {
            ManagerDetailResponse manager = managerService.getByUsernameAndPassword(username, password);
            return ResponseEntity.ok().body(ApiResponse.success(manager.getId()));
        }
    }

}
