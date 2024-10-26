package com.hackathon.bankingapp.controller;


import com.hackathon.bankingapp.dto.request.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.UserRegisterResponse;
import com.hackathon.bankingapp.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/users/register")
    public ResponseEntity<UserRegisterResponse> registerUser(@RequestBody @Valid
                                                             UserRegisterRequest userRegisterRequest) {

        return ResponseEntity.ok(authenticationService.registerUser(userRegisterRequest));
    }

    @GetMapping
    public ResponseEntity<String> sayHi() {
        return ResponseEntity.ok("2");
    }
}
