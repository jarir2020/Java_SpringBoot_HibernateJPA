package com.jarirahmed.springboot.error;

import com.jarirahmed.springmvc.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/** Boot-specific advice for the file API while reusing Phase 3's error shape. */
@RestControllerAdvice
public class BootExceptionHandler {
    @ExceptionHandler(AttachmentNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            AttachmentNotFoundException exception,
            HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(EmptyAttachmentException.class)
    public ResponseEntity<ApiError> handleEmpty(
            EmptyAttachmentException exception,
            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String message,
            HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                Map.of()));
    }
}
