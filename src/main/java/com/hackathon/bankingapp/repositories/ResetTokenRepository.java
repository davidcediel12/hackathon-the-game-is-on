package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
}