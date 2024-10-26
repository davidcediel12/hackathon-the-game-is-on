package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.dto.request.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.UserRegisterResponse;

public interface AuthenticationService {

    UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest);
}
