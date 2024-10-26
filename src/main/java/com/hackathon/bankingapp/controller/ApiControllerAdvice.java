package com.hackathon.bankingapp.controller;

import com.hackathon.bankingapp.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApiException(ApiException apiException) {
        return new ResponseEntity<>(apiException.getMessage(), apiException.getStatus());
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException badCredentialsException) {
        return new ResponseEntity<>("Bad credentials", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
