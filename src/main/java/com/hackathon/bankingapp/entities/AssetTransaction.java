package com.hackathon.bankingapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "asset_transaction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, precision = 38, scale = 16)
    private BigDecimal amount;
    @Column(nullable = false, precision = 38, scale = 16)
    private BigDecimal price;

    @Column(nullable = false, precision = 38, scale = 16)
    private BigDecimal transactionValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetTransactionType transactionType;

    @ManyToOne
    private Asset asset;
}