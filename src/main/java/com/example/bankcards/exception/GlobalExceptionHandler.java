package com.example.bankcards.exception;

import generated.com.example.bankcards.api.model.ApiError;
import generated.com.example.bankcards.api.model.ExceptionBody;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ExceptionBody> handleEntityExistsException(EntityExistsException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("AlreadyExists", ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleEntityNotFoundException(EntityNotFoundException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Not Found", ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(EntityBlockedException.class)
    public ResponseEntity<ExceptionBody> handleEntityBlockedException(EntityBlockedException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Entity blocked", ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionBody> handleAccessDeniedException(AccessDeniedException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Access denied", ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ExceptionBody> handleRollbackException(RollbackException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof jakarta.validation.ConstraintViolationException violationEx) {
            List<ApiError> errors = violationEx.getConstraintViolations().stream()
                    .map(v -> new ApiError(v.getPropertyPath().toString(), v.getMessage()))
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionBody(errors));
        }
        return handleAll(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBody> handleAll(Exception ex) {
        ex.printStackTrace();
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("Internal server error", "something went wrong"))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
