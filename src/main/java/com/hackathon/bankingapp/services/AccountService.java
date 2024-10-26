package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.User;

public interface AccountService {

    Account createAccount(User user);

}
