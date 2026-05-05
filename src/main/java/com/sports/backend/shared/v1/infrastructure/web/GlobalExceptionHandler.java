package com.sports.backend.shared.v1.infrastructure.web;

import com.sports.backend.shared.v1.application.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(final ApplicationException ex) {
        log.warn("Application error: {} - {}", ex.getError().name(), ex.getMessage());
        return ResponseEntity
                .status(ex.getError().getHttpStatus())
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "error", ex.getError().name(),
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(final Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .internalServerError()
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "error", "INTERNAL_ERROR",
                        "message", "An unexpected error occurred"
                ));
    }
}
