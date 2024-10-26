package com.hackathon.bankingapp.entities;


import jakarta.persistence.*;

@Entity
@Table
public class User {

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
}
