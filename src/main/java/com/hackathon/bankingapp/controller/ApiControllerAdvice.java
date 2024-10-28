package com.hackathon.bankingapp.controller;

import com.hackathon.bankingapp.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    public ResponseEntity<String> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {

        List<String> errorsOrder = List.of("Password", "Email");

        List<ObjectError> errors = exception.getBindingResult().getAllErrors()
                .stream().sorted(Comparator.comparingInt(fieldError -> errorsOrder.indexOf(fieldError.getCode())))
                .toList();


        for (ObjectError error : errors) {
            ResponseEntity<String> fieldError = getCustomErrorMessage(error);
            if (fieldError != null) {
                return fieldError;
            }


        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<String> getCustomErrorMessage(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            if (Objects.equals(fieldError.getCode(), "Password")) {

                return new ResponseEntity<>(fieldError.getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            if (Objects.equals(fieldError.getCode(), "Email")) {

                return new ResponseEntity<>("Invalid email: " + fieldError.getRejectedValue(), HttpStatus.BAD_REQUEST);
            }

            if (Objects.equals(fieldError.getField(), "pin")) {
                Object rejectedValue = fieldError.getRejectedValue();
                if (rejectedValue == null || rejectedValue.toString().isBlank()) {
                    return new ResponseEntity<>("PIN cannot be null or empty", HttpStatus.BAD_REQUEST);
                }
            }
        }
        return null;
    }
}
