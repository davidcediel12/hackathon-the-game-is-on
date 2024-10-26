package com.hackathon.bankingapp.controller;


import com.hackathon.bankingapp.dto.request.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.UserRegisterResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @PostMapping("/users/register")
    public ResponseEntity<UserRegisterResponse> registerUser(@RequestBody @Valid
                                                                 UserRegisterRequest userRegisterRequest){

        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity<String> sayHi(){
        return ResponseEntity.ok("2");
    }
}