package com.hackathon.bankingapp.services.customer;

import com.hackathon.bankingapp.dto.request.authentication.AssignPinRequest;
import com.hackathon.bankingapp.dto.request.authentication.UpdatePinRequest;
import com.hackathon.bankingapp.dto.response.dashboard.AccountDetailResponse;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.User;

public interface AccountService {

    Account createAccount(User user);

    AccountDetailResponse getLoggedInUserAccount();

    void assignPin(AssignPinRequest assignPinRequest);
    void updatePin(UpdatePinRequest assignPinRequest);

    Account getUserAccount();
}
