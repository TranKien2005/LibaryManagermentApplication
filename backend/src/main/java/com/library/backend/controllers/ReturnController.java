package com.library.backend.controllers;

import com.library.backend.dtos.requests.ReturnCreationRequest;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.ReturnDetailResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/returnss")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnController {

    @PostMapping
    ResponseEntity<ApiResponse<ReturnDetailResponse>> create(
            @RequestBody ReturnCreationRequest request
    ) {
        return null;
    }

}
