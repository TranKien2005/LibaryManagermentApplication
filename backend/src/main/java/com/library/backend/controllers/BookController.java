package com.library.backend.controllers;

import com.library.backend.dtos.requests.BookCreationRequest;
import com.library.backend.dtos.requests.BookUpdateRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.BookDetailResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/bookss")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    @PostMapping("/data")
    ResponseEntity<ApiResponse<BookDetailResponse>> createByData(
            @RequestBody BookCreationRequest request
    ) {
        return null;
    }

    @PostMapping("/isbn/{isbnCode}")
    ResponseEntity<ApiResponse<BookDetailResponse>> createByISBN(
            @PathVariable String isbnCode
    ) {
        return null;
    }

    @PostMapping("/upload")
    ResponseEntity<ApiResponse<List<BookDetailResponse>>> createByTxt(
            @RequestParam("file")MultipartFile file
    ) {
        return null;
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<BookDetailResponse>> getById(
            @PathVariable Integer id
    ) {
        return null;
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<BookDetailResponse>>> getBooks(
            @RequestParam(required = false) String sorted,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset
    ) {
        return null;
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable Integer id
    ) {
        return null;
    }

    @PostMapping("/update/{id}")
    ResponseEntity<ApiResponse<BookDetailResponse>> update(
            @PathVariable Integer id,
            @RequestBody BookUpdateRequest request
    ) {
        return null;
    }


}
