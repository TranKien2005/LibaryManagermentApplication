package com.library.backend.controllers.v1;

import com.library.backend.dtos.requests.BorrowCreationRequest;
import com.library.backend.dtos.requests.BorrowUpdateRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.BorrowDetailResponse;
import com.library.backend.entities.Borrow;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.services.BorrowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrows")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1BorrowService {

    BorrowService borrowService;

    @GetMapping
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        List<BorrowDetailResponse> list = borrowService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(
                        response -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("borrowID", response.getId());
                            map.put("accountID", response.getStudent().getId());
                            map.put("bookID", response.getBook().getId());
                            map.put("borrowDate", response.getBorrowDate());
                            map.put("expectedReturnDate", response.getExpectedReturnDate());
                            map.put("status", response.getStatus());
                            return map;
                        }
                ).toList()
        ));
    }

    @PostMapping
    ResponseEntity<ApiResponse<Void>> insert(
            @RequestBody Map<String, Object> request
    ) {
        BorrowCreationRequest borrowCreationRequest = BorrowCreationRequest.builder()
                .bookId((Integer) request.get("bookID"))
                .studentId((Integer) request.get("accountID"))
                .expectedReturnDate((LocalDate) request.get("expectedReturnDate"))
                .build();
        BorrowDetailResponse response = borrowService.create(borrowCreationRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        BorrowUpdateRequest borrowUpdateRequest = BorrowUpdateRequest.builder()
                .expectedReturnDate((LocalDate) request.get("expectedReturnDate"))
                .status((Borrow.Type) request.get("status"))
                .build();
        BorrowDetailResponse response = borrowService.update(id, borrowUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {
        borrowService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<Map<String, Object>>> get(
            @PathVariable Integer id
    ) {
        BorrowDetailResponse response = borrowService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("borrowID", response.getId());
        map.put("accountID", response.getStudent().getId());
        map.put("bookID", response.getBook().getId());
        map.put("borrowDate", response.getBorrowDate());
        map.put("expectedReturnDate", response.getExpectedReturnDate());
        map.put("status", response.getStatus());
        return ResponseEntity.ok().body(ApiResponse.success(map));
    }



    @PostMapping("/get-id")
    ResponseEntity<ApiResponse<Integer>> create(
            @RequestBody Map<String, Object> request
    ) {
        BorrowDetailResponse response = borrowService.find(
                (Integer) request.get("accountID"),
                (Integer) request.get("bookID"),
                (LocalDate) request.get("borrowDate"),
                (LocalDate) request.get("expectedReturnDate"),
                (Borrow.Type) request.get("status")
        );
        return ResponseEntity.ok().body(ApiResponse.success(response.getId()));
    }

    @GetMapping("/ids")
    ResponseEntity<ApiResponse<List<Integer>>> getAllIds() {
        List<Integer> ids = borrowService.getAllIds();
        return ResponseEntity.ok().body(ApiResponse.success(ids));
    }
}
