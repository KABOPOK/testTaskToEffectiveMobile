package com.example.bankcards.exception;

import generated.com.example.bankcards.api.model.ApiError;
import generated.com.example.bankcards.api.model.ExceptionBody;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.RollbackException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ExceptionBody> handleEntityExistsException(EntityExistsException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("AlreadyExists", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleEntityNotFoundException(EntityNotFoundException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Not Found", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(EntityBlockedException.class)
    public ResponseEntity<ExceptionBody> handleEntityBlockedException(EntityBlockedException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Entity blocked", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionBody> handleAccessDeniedException(AccessDeniedException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Access denied", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionBody> handleValidationException(ValidationException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Validation error", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBody> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Validation error", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ExceptionBody> handleRollbackException(RollbackException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof jakarta.validation.ConstraintViolationException violationEx) {
            List<ApiError> errors = violationEx.getConstraintViolations().stream()
                    .map(v -> new ApiError(v.getPropertyPath().toString(), v.getMessage()))
                    .toList();
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionBody(errors));
        }
        log.error(ex.getMessage(), ex);
        return handleAll(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBody> handleAll(Exception ex) {
        ex.printStackTrace();
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Internal server error", "something went wrong"))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
