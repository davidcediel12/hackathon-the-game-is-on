package com.hackathon.bankingapp.services.impl;

import com.hackathon.bankingapp.dto.request.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.UserRegisterResponse;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository userRepository;


    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest) {
        return null;
    }
}
