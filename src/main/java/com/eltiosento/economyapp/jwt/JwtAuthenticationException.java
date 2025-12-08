package com.eltiosento.economyapp.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.eltiosento.economyapp.error.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationException implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // Creem un objecte ApiError
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "You are not authorized to access this resource");

        // Configurem la resposta HTTP
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized

        // Convertim l'objecte ApiError a JSON
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
        response.getWriter().flush();
    }

}
