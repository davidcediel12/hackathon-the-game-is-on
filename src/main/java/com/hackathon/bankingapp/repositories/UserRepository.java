package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String username);

    User getReferenceByEmailIgnoreCase(String email);
}