package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
}