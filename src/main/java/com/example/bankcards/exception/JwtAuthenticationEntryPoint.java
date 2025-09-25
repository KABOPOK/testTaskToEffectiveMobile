package com.example.bankcards.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.com.example.bankcards.api.model.ApiError;
import generated.com.example.bankcards.api.model.ExceptionBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ExceptionBody body = new ExceptionBody(
                List.of(new ApiError("AuthenticationFailed", "You are not authenticated or Wrong password/login"))
        );
        authException.printStackTrace();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

