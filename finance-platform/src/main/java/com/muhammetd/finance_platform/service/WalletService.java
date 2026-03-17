package com.muhammetd.finance_platform.service;

import com.muhammetd.finance_platform.model.Wallet;
import com.muhammetd.finance_platform.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void lockBalanceForOrder(Long userId, String assetSymbol, BigDecimal amountToLock) {
        Wallet wallet = walletRepository.findByUserIdAndAssetSymbol(userId, assetSymbol)
                .orElseThrow(() -> new RuntimeException("Cüzdan Bulunamadı"));

        if (wallet.getAvailableBalance().compareTo(amountToLock) < 0) {
            throw new RuntimeException("Yetersiz Bakiye");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amountToLock));

        wallet.setLockedBalance(wallet.getLockedBalance().add(amountToLock));

        walletRepository.save(wallet);

    }

    @Transactional
    public void unlockBalanceForOrder(Long userId, String assetSymbol, BigDecimal amountToLock) {

        Wallet wallet = walletRepository.findByUserIdAndAssetSymbol(userId, assetSymbol)
                .orElseThrow(() -> new RuntimeException("Cüzdan bulunamadı!"));

        if (wallet.getLockedBalance().compareTo(amountToLock) < 0) {
            throw new RuntimeException("Kilitli bakiye yetersiz, tutarsızlık var");
        }

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(amountToLock));

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amountToLock));

        walletRepository.save(wallet);

    }

    @Transactional
    public void deductLockedBalance(Long userId, String assetSymbol, BigDecimal amountToDeduct) {

        Wallet wallet = walletRepository.findByUserIdAndAssetSymbol(userId, assetSymbol)
                .orElseThrow(() -> new RuntimeException("Cüzdan bulunamadı!"));

        if (wallet.getLockedBalance().compareTo(amountToDeduct) < 0) {
            throw new RuntimeException("Düşülecek miktar kilitli bakiyeden fazla olamaz!");
        }

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(amountToDeduct));

        walletRepository.save(wallet);
    }


}
