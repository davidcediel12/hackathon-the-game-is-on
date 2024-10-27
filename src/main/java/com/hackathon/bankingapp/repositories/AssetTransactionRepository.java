package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.Asset;
import com.hackathon.bankingapp.entities.AssetTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetTransactionRepository extends JpaRepository<AssetTransaction, Long> {
    List<AssetTransaction> findByAsset(Asset asset);
}