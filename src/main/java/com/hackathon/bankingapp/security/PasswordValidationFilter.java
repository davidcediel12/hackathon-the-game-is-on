package com.hackathon.bankingapp.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class PasswordValidationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean isPinEndpoint = request.getRequestURI().startsWith("/api/account/pin");
        if (!isPinEndpoint) {
            doFilter(request, response, filterChain);
            return;
        }

        try {
            String bodyString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            Map<String, String> body = objectMapper.readValue(
                    bodyString, new TypeReference<Map<String, String>>() {
                    });

            ApiException invalidPassword = new ApiException("Invalid password", HttpStatus.BAD_REQUEST);
            if (!body.containsKey("password")) {
                throw invalidPassword;
            }

            String encryptedPassword = passwordEncoder.encode(body.get("password"));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            if (!Objects.equals(encryptedPassword, user.getPassword())) {
                throw invalidPassword;
            }
            doFilter(request, response, filterChain);
        } catch (RuntimeException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
