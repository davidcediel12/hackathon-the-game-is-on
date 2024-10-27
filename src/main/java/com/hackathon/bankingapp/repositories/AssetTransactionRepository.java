package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.AssetTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetTransactionRepository extends JpaRepository<AssetTransaction, Long> {
}