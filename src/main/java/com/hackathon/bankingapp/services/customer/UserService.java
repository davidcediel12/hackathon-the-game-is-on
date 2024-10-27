package com.hackathon.bankingapp.services.customer;

import com.hackathon.bankingapp.dto.response.dashboard.UserDetailsResponse;

public interface UserService {


    UserDetailsResponse getLoggedInUserDetails();
}
