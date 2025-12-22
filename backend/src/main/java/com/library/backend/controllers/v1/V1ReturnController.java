package com.library.backend.controllers.v1;

import com.library.backend.dtos.requests.ReturnCreationRequest;
import com.library.backend.dtos.requests.ReturnUpdateRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.ReturnDetailResponse;
import com.library.backend.services.ReturnService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1ReturnController {

    ReturnService returnService;

    @GetMapping
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        List<ReturnDetailResponse> list = returnService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(
                        response -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("returnID", response.getId());
                            map.put("borrowID", response.getBorrow().getId());
                            map.put("returnDate", response.getReturnDate());
                            map.put("damagePercentage", response.getDamagePercentage());
                            return map;
                        }
                ).toList()
        ));
    }

    @PostMapping
    ResponseEntity<ApiResponse<Void>> insert(
            @RequestBody Map<String, Object> request
    ) {
        System.out.println(request);
        ReturnCreationRequest returnCreationRequest = ReturnCreationRequest.builder()
                .borrowId((Integer) request.get("borrowID"))
                .damagePercentage((Integer) request.get("damagePercentage"))
                .build();
        System.out.println(returnCreationRequest);
        ReturnDetailResponse response = returnService.create(returnCreationRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        ReturnUpdateRequest returnUpdateRequest = ReturnUpdateRequest.builder()
                .damagePercentage((Integer) request.get("damagePercentage"))
                .build();
        ReturnDetailResponse response = returnService.update(id, returnUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {
        returnService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<Map<String, Object>>> get(
            @PathVariable Integer id
    ) {
        ReturnDetailResponse response = returnService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("returnID", response.getId());
        map.put("borrowID", response.getBorrow().getId());
        map.put("returnDate", response.getReturnDate());
        map.put("damagePercentage", response.getDamagePercentage());
        return ResponseEntity.ok().body(ApiResponse.success(map));
    }
}
