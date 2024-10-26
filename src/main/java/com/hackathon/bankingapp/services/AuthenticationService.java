package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.dto.request.LoginRequest;
import com.hackathon.bankingapp.dto.request.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.Token;
import com.hackathon.bankingapp.dto.response.UserDetailsResponse;

public interface AuthenticationService {


    UserDetailsResponse registerUser(UserRegisterRequest userRegisterRequest);

    Token login(LoginRequest loginRequest);
}
