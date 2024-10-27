package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}