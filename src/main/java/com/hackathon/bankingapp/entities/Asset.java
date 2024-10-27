package com.hackathon.bankingapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "asset")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String assetSymbol;

    @Column(nullable = false, precision = 38, scale = 16)
    private BigDecimal assetAmount;

    @Column(nullable = false, precision = 38, scale = 16)
    private BigDecimal totalAssetBought;

    @Column(nullable = false, precision = 38, scale = 16)
    private BigDecimal averagePriceBought;

    @ManyToOne(optional = false)
    private Account account;
}