package com.library.backend.controllers;

import com.library.backend.dtos.requests.BorrowCreationRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.BorrowDetailResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowss")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowController {

    @GetMapping("/user/{id}")
    ResponseEntity<ApiResponse<List<BorrowDetailResponse>>> getByStudentId(
            @PathVariable Integer id
    ) {
        return null;
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<BorrowDetailResponse>> getById(
            @PathVariable Integer id
    ) {
        return null;
    }

    @GetMapping("/check")
    ResponseEntity<ApiResponse<Object>> check(
            @RequestParam Integer studentId,
            @RequestParam Integer bookId
    ) {
        return null;
    }

    @PostMapping
    ResponseEntity<ApiResponse<BorrowDetailResponse>> create(
            @RequestBody BorrowCreationRequest request
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
