package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    @Query("select o from OtpCode o where upper(o.user.email) = upper(?1) and o.code = ?2 and o.used = false")
    Optional<OtpCode> findUnusedOtpCodeByUserEmail(String email, String code);
}