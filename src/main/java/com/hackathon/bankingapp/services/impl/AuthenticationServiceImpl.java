package com.hackathon.bankingapp.services.impl;

import com.hackathon.bankingapp.dto.request.LoginRequest;
import com.hackathon.bankingapp.dto.request.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.Token;
import com.hackathon.bankingapp.dto.response.UserRegisterResponse;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Override
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest) {
        validateEmailAndPhoneuniqueness(userRegisterRequest);

        String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());
        UUID accountNumber = UUID.randomUUID();

        User user = createUser(userRegisterRequest, encodedPassword, accountNumber);
        userRepository.save(user);


        return createUserResponse(userRegisterRequest, encodedPassword, accountNumber);
    }

    @Override
    public Token login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password()));

        User userDetails = (User) authentication.getPrincipal();

        return null;
    }


    private void validateEmailAndPhoneuniqueness(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmailIgnoreCase(userRegisterRequest.email())) {
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByPhoneNumber(userRegisterRequest.phoneNumber())) {
            throw new ApiException("Phone number already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private UserRegisterResponse createUserResponse(UserRegisterRequest userRegisterRequest,
                                                    String encodedPassword, UUID accountNumber) {

        return new UserRegisterResponse(userRegisterRequest.name(), encodedPassword,
                userRegisterRequest.email(), userRegisterRequest.address(), userRegisterRequest.phoneNumber(),
                accountNumber.toString(), encodedPassword);
    }

    private User createUser(UserRegisterRequest userRegisterRequest, String encodedPassword,
                            UUID accountNumber) {

        return User.builder()
                .name(userRegisterRequest.name())
                .address(userRegisterRequest.address())
                .email(userRegisterRequest.email())
                .password(encodedPassword)
                .phoneNumber(userRegisterRequest.phoneNumber())
                .accountNumber(accountNumber.toString())
                .build();
    }
}
