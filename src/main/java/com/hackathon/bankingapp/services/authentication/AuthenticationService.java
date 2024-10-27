package com.hackathon.bankingapp.services.authentication;

import com.hackathon.bankingapp.dto.request.authentication.LoginRequest;
import com.hackathon.bankingapp.dto.request.authentication.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.authentication.Token;
import com.hackathon.bankingapp.dto.response.dashboard.UserDetailsResponse;

public interface AuthenticationService {


    UserDetailsResponse registerUser(UserRegisterRequest userRegisterRequest);

    Token login(LoginRequest loginRequest);

    void logout(String token);
}
