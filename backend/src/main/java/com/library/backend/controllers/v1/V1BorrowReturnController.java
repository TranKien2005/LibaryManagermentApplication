package com.library.backend.controllers.v1;

import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.ReturnDetailResponse;
import com.library.backend.services.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("borrow-returns")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1BorrowReturnController {

    BorrowService borrowService;

    @GetMapping
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
    List<ReturnDetailResponse> list = borrowService.getAllBorrowReturns();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(
                        response -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("borrowID", response.getBorrow().getId());
                            map.put("member", response.getBorrow().getStudent().getId() + "-" + response.getBorrow().getStudent().getFullName());
                            map.put("book", response.getBorrow().getBook().getId() + "-" + response.getBorrow().getBook().getTitle());
                            map.put("borrowDate", response.getBorrow().getBorrowDate());
                            map.put("expectedReturnDate", response.getBorrow().getExpectedReturnDate());
                            map.put("status", response.getBorrow().getStatus());
                            map.put("returnDate", response.getReturnDate());
                            map.put("damagePercentage", response.getDamagePercentage());
                            LocalDate expected = response.getBorrow().getExpectedReturnDate();
                            LocalDate returned = response.getReturnDate();
                            Integer damage = response.getDamagePercentage();
                            int penaltyFee = 0;
                            if (returned != null) {
                                if (returned.isAfter(expected)) {
                                    long daysLate = ChronoUnit.DAYS.between(expected, returned);
                                    penaltyFee = (int) (daysLate * 1000 + (damage != null ? damage : 0) * 5000);
                                } else {
                                    penaltyFee = (damage != null ? damage : 0) * 5000;
                                }
                            }
                            map.put("penaltyFee", penaltyFee);
                            return map;
                        }
                ).toList()
        ));
    }

    @GetMapping("/by-account/{accountId}")
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getByAccountId(
        @PathVariable Integer accountId
    ) {
    List<ReturnDetailResponse> list = borrowService.getBorrowReturnsByStudentId(accountId);
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(
                        response -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("borrowID", response.getBorrow().getId());
                            map.put("member", response.getBorrow().getStudent().getId() + "-" + response.getBorrow().getStudent().getFullName());
                            map.put("book", response.getBorrow().getBook().getId() + "-" + response.getBorrow().getBook().getTitle());
                            map.put("borrowDate", response.getBorrow().getBorrowDate());
                            map.put("expectedReturnDate", response.getBorrow().getExpectedReturnDate());
                            map.put("status", response.getBorrow().getStatus());
                            map.put("returnDate", response.getReturnDate());
                            map.put("damagePercentage", response.getDamagePercentage());
                            LocalDate expected = response.getBorrow().getExpectedReturnDate();
                            LocalDate returned = response.getReturnDate();
                            Integer damage = response.getDamagePercentage();
                            int penaltyFee = 0;
                            if (returned != null) {
                                if (returned.isAfter(expected)) {
                                    long daysLate = ChronoUnit.DAYS.between(expected, returned);
                                    penaltyFee = (int) (daysLate * 1000 + (damage != null ? damage : 0) * 5000);
                                } else {
                                    penaltyFee = (damage != null ? damage : 0) * 5000;
                                }
                            }
                            map.put("penaltyFee", penaltyFee);
                            return map;
                        }
                ).toList()
        ));
    }

    @GetMapping("/is-borrowed")
    ResponseEntity<ApiResponse<Boolean>> isBorrowed(
            @RequestParam Integer accountId,
            @RequestParam Integer bookId
    ) {
        Boolean out = borrowService.isBorrowed(accountId, bookId);
        return ResponseEntity.ok().body(ApiResponse.success(out));
    }


}
