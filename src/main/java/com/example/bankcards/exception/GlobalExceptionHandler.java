package com.example.bankcards.exception;

import com.example.bankcards.dto.ApiErrorDto;
import com.example.bankcards.dto.ExceptionBodyDto;
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
    public ResponseEntity<ExceptionBodyDto> handleEntityExistsException(EntityExistsException ex) {
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("AlreadyExists", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionBodyDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("Not Found", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(EntityBlockedException.class)
    public ResponseEntity<ExceptionBodyDto> handleEntityBlockedException(EntityBlockedException ex) {
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("Entity blocked", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionBodyDto> handleAccessDeniedException(AccessDeniedException ex) {
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("Access denied", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionBodyDto> handleValidationException(ValidationException ex) {
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("Validation error", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBodyDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("Validation error", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionBodyDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("Validation error", ex.getMessage()))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ExceptionBodyDto> handleRollbackException(RollbackException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof jakarta.validation.ConstraintViolationException violationEx) {
            List<ApiErrorDto> errors = violationEx.getConstraintViolations().stream()
                    .map(v -> new ApiErrorDto(v.getPropertyPath().toString(), v.getMessage()))
                    .toList();
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionBodyDto(errors));
        }
        log.error(ex.getMessage(), ex);
        return handleAll(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBodyDto> handleAll(Exception ex) {
        ex.printStackTrace();
        ExceptionBodyDto body = new ExceptionBodyDto(
                List.of(new ApiErrorDto("Internal server error", "something went wrong"))
        );
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
