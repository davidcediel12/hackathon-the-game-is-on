package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}