package com.muhammetd.finance_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "asset_symbol"})
})
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "asset_symbol", nullable = false, length = 10)
    private String assetSymbol;

    @Column(precision = 24, scale = 8, nullable = false)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(precision = 24, scale = 8, nullable = false)
    private BigDecimal lockedBalance = BigDecimal.ZERO;

    public Wallet() {

    }

    public Wallet(Long id, User user, String assetSymbol, BigDecimal availableBalance,
                  BigDecimal lockedBalance) {
        this.id = id;
        this.user = user;
        this.assetSymbol = assetSymbol;
        this.availableBalance = availableBalance;
        this.lockedBalance = lockedBalance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimal getLockedBalance() {
        return lockedBalance;
    }

    public void setLockedBalance(BigDecimal lockedBalance) {
        this.lockedBalance = lockedBalance;
    }
}
