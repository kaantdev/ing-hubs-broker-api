package com.ing.hubs.broker_api.config;

import com.ing.hubs.broker_api.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.xml.bind.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(0)
@ControllerAdvice
public class GlobalExceptionHandler {

    //Handles validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        String errorMessage = "Validation failed for the request.";
        return ResponseEntity.badRequest().body(buildErrorResponse(errorMessage, errors, HttpStatus.BAD_REQUEST));
    }

    //Handles constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage(), null, HttpStatus.BAD_REQUEST));
    }

    //Handles all generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ErrorResponse buildErrorResponse(String message, Map<String, String> errors, HttpStatus status) {
        return ErrorResponse.builder()
                .message(message)
                .status(status)
                .dateTime(LocalDateTime.now())
                .fieldErrors(errors)
                .build();
    }
}
