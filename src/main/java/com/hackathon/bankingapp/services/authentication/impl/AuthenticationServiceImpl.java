package com.hackathon.bankingapp.services.authentication.impl;

import com.hackathon.bankingapp.dto.request.authentication.LoginRequest;
import com.hackathon.bankingapp.dto.request.authentication.UserRegisterRequest;
import com.hackathon.bankingapp.dto.response.authentication.Token;
import com.hackathon.bankingapp.dto.response.dashboard.UserDetailsResponse;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.security.JwtBlacklistManager;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.authentication.AuthenticationService;
import com.hackathon.bankingapp.services.authentication.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AccountService accountService;
    private final JwtBlacklistManager jwtBlacklistManager;


    @Override
    @Transactional
    public UserDetailsResponse registerUser(UserRegisterRequest userRegisterRequest) {
        validateEmailAndPhoneUniqueness(userRegisterRequest);

        String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());

        User user = createUserAndAccount(userRegisterRequest, encodedPassword);


        return createUserResponse(userRegisterRequest, encodedPassword, user.getAccount().getAccountId());
    }

    @Override
    public Token login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password()));

        User userDetails = (User) authentication.getPrincipal();

        String token = tokenService.generateToken(userDetails);

        return new Token(token);
    }

    @Override
    public void logout(String token) {
        jwtBlacklistManager.addTokenToBlackList(token);
    }


    private void validateEmailAndPhoneUniqueness(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmailIgnoreCase(userRegisterRequest.email())) {
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByPhoneNumber(userRegisterRequest.phoneNumber())) {
            throw new ApiException("Phone number already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private UserDetailsResponse createUserResponse(UserRegisterRequest userRegisterRequest,
                                                   String encodedPassword, String accountNumber) {

        return new UserDetailsResponse(userRegisterRequest.name(),
                userRegisterRequest.email(), userRegisterRequest.address(), userRegisterRequest.phoneNumber(),
                accountNumber, encodedPassword);
    }

    private User createUserAndAccount(UserRegisterRequest userRegisterRequest, String encodedPassword) {


        User user = User.builder()
                .name(userRegisterRequest.name())
                .address(userRegisterRequest.address())
                .email(userRegisterRequest.email())
                .password(encodedPassword)
                .phoneNumber(userRegisterRequest.phoneNumber())
                .build();

        user = userRepository.save(user);
        user.setAccount(accountService.createAccount(user));
        return user;
    }
}
