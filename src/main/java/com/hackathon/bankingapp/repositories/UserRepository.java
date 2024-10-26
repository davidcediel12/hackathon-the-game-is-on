package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}