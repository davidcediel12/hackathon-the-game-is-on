package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {

    @Query("select r from ResetToken r where upper(r.user.email) = upper(?1) and r.token = ?2 and r.used = false")
    Optional<ResetToken> findUnusedTokenByUserEmail(String email, String resetToken);
}