package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByAssetSymbolAndAccount(String assetSymbol, Account account);

    List<Asset> findByAccount(Account account);
}