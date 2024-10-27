package com.hackathon.bankingapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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
    private Long id;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private BigDecimal balance;

    @OneToOne(optional = false)
    private User user;

    @Column(length = 4)
    private String pin;

    @OneToMany(mappedBy = "sourceAccount")
    private List<Transaction> transactions;
}