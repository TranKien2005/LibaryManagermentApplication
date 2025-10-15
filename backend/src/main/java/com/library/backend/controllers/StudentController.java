package com.library.backend.controllers;

import com.library.backend.dtos.requests.StudentCreationRequest;
import com.library.backend.dtos.requests.StudentUpdateRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.StudentDetailResponse;
import com.library.backend.services.StudentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studentss")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentController {

    StudentService studentService;

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<StudentDetailResponse>> getById(
            @PathVariable Integer id
    ) {
        StudentDetailResponse response = studentService.getById(id);
        return ResponseEntity.ok().body(
                ApiResponse.success(response)
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<StudentDetailResponse>> getStudents(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset
    ) {
        return null;
    }

    @PostMapping
    ResponseEntity<ApiResponse<StudentDetailResponse>> create(
            @RequestBody StudentCreationRequest request
            ) {
        return null;
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<StudentDetailResponse>> update(
            @PathVariable Integer id,
            @RequestBody StudentUpdateRequest request
    ) {
        return null;
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable Integer id
    ) {
        return null;
    }

}
