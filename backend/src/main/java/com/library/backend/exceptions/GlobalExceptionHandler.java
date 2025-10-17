package com.library.backend.exceptions;

import com.library.backend.dtos.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e) {
        e.printStackTrace();
        ResponseCode code = e.getCode();
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(code.getMessage())
                .build();
        return ResponseEntity.status(400).body(response);
    }
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e) {
e.printStackTrace();
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(ResponseCode.UNKNOWN_ERROR.getMessage())
                .build();
        return ResponseEntity.status(500).body(response);
    }

}
