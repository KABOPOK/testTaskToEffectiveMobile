package com.example.bankcards.exception;

import generated.com.example.bankcards.api.model.ApiError;
import generated.com.example.bankcards.api.model.ExceptionBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InstanceAlreadyExistsException.class)
    public ResponseEntity<ExceptionBody> handleInstanceAlreadyExists(InstanceAlreadyExistsException ex) {
        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("AlreadyExists", ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
