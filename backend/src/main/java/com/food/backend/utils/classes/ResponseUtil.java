package com.food.backend.utils.classes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<ApiResponse<T>> successResponse(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(data, message, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFoundResponse(String message) {
        ApiResponse<T> response = new ApiResponse<>(null, message, HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequestResponse(String message) {
        ApiResponse<T> response = new ApiResponse<>(null, message, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }
}