package com.hackathon.bankingapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {


    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    String accountId;

    @Column(nullable = false)
    BigDecimal balance;

    @OneToOne
    User user;
}