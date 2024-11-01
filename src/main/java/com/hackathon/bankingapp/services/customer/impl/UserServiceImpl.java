package com.hackathon.bankingapp.services.customer.impl;

import com.hackathon.bankingapp.dto.response.dashboard.UserDetailsResponse;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.services.customer.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ApiException("User not found for the given identifier: " + username,
                                new UsernameNotFoundException(username), HttpStatus.BAD_REQUEST));
    }

    @Override
    public UserDetailsResponse getLoggedInUserDetails() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return new UserDetailsResponse(user.getName(),
                user.getUsername(), user.getEmail(), user.getPhoneNumber(),
                user.getAccount().getAccountId(), user.getPassword());
    }
}
