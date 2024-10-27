package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}