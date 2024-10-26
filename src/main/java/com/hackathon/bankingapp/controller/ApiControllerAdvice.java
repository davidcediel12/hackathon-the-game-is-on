package com.hackathon.bankingapp.controller;

import com.hackathon.bankingapp.exceptions.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApiException(ApiException apiException){
        return new ResponseEntity<>(apiException.getMessage(), apiException.getStatus());
    }
}
