package com.muhammetd.finance_platform.repository;

import com.muhammetd.finance_platform.model.User;
import com.muhammetd.finance_platform.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByUserIdAndAssetSymbol(Long userId, String assetSymbol);

}
