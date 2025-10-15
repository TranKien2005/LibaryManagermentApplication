package com.library.backend.exceptions;

import com.library.backend.dtos.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e) {
        ResponseCode code = e.getCode();
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(code.getMessage())
                .build();
        return ResponseEntity.status(400).body(response);
    }

    ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(ResponseCode.UNKNOWN_ERROR.getMessage())
                .build();
        return ResponseEntity.status(500).body(response);
    }

}
