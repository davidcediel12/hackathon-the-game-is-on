package com.hackathon.bankingapp.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String password;
    @Column(nullable = false, unique = true)
    String email;
    @Column(nullable = false)
    String address;
    @Column(nullable = false, unique = true)
    String phoneNumber;

    @Column(nullable = false, unique = true)
    String accountNumber;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
