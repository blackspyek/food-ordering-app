package com.food.backend.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.Role;
import org.apache.coyote.BadRequestException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
@Order(0)
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) throws JsonProcessingException {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        Map<String, List<String>> errors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();

            errors.computeIfAbsent(field, _ -> new ArrayList<>()).add(message);
        }

        for (ObjectError objectError : bindingResult.getGlobalErrors()) {
            String message = objectError.getDefaultMessage();
            errors.computeIfAbsent("message", _ -> new ArrayList<>()).add(message);
        }

        return getResponseEntity("Validation failed", errors);
    }

    public static ResponseEntity<String> getResponseEntity(String message, Object errors) throws JsonProcessingException {
        Map<String, Object> errorMap = Map.of(
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", message,
                "status", HttpStatus.BAD_REQUEST.value(),
                "errors", errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(errorMap));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidEnum(HttpMessageNotReadableException e) throws JsonProcessingException {
        String message;
        message = getValidMessageFromExceptionMessage(e);
        return getResponseEntity("Invalid Enum", message);
    }

    private static String getValidMessageFromExceptionMessage(HttpMessageNotReadableException e) {
        String errorType = getErrorTypeFromExceptionMessage(e);
        return switch (errorType) {
            case "Category" -> "Invalid Category. Allowed values are " + Arrays.toString(Category.values());
            case "Role" -> "Invalid Role. Allowed values are " + Arrays.toString(Role.values());
            default -> "Invalid Enum";
        };
    }

    private static String getErrorTypeFromExceptionMessage(HttpMessageNotReadableException e) {
        String errorType;
        if (e.getMessage().contains("Category")) {
            errorType = "Category";
        } else if (e.getMessage().contains("Role")) {
            errorType = "Role";
        } else {
            errorType = "Other";
        }
        return errorType;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(Exception e) throws JsonProcessingException {
        return getResponseEntity("Invalid Enum", e.getMessage());
    }


}