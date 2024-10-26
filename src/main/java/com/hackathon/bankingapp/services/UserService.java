package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.dto.response.UserDetailsResponse;

public interface UserService {


    UserDetailsResponse getLoggedInUserDetails();
}
