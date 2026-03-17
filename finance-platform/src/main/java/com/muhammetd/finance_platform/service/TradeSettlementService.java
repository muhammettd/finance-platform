package com.muhammetd.finance_platform.service;

import com.muhammetd.finance_platform.dto.TradeEvent;
import com.muhammetd.finance_platform.dto.response.TradeResponse;
import com.muhammetd.finance_platform.model.TradeOrder;
import com.muhammetd.finance_platform.model.Wallet;
import com.muhammetd.finance_platform.repository.TradeOrderRepository;
import com.muhammetd.finance_platform.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Service
public class TradeSettlementService {

    private static final Logger log = LoggerFactory.getLogger(TradeSettlementService.class);
    private final TradeOrderRepository orderRepository;
    private final WalletRepository walletRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public TradeSettlementService(TradeOrderRepository orderRepository, WalletRepository walletRepository, SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * It constantly listens to the "trades.topic" channel on Kafka.
     * * This method is triggered when a new message (Event) arrives.
     */

    @KafkaListener(topics = "trades.topic", groupId = "trade-settlement-group")
    @Transactional
    public void processTradeSettlement(String eventJson) {

        TradeEvent event = objectMapper.readValue(eventJson, TradeEvent.class);

        log.info("Kafkadan işlem alındı: Alıcı ID {}, Satıcı ID {}, Miktar {}", event.getBuyerOrderId(), event.getSellerOrderId(), event.getQuantity());

        try {
            TradeOrder buyerOrder = orderRepository.findById(event.getBuyerOrderId())
                    .orElseThrow(() -> new RuntimeException("Alıcı emri bulunamadı."));
            TradeOrder sellerOrder = orderRepository.findById(event.getSellerOrderId())
                    .orElseThrow(() -> new RuntimeException("Satıcı emri bulunamadı"));


            String takerSide = "BUY";
            if (sellerOrder.getId() > buyerOrder.getId()) {
                takerSide = "SELL";
            }

            String[] symbols = event.getSymbolPair().split("_");
            String baseAsset = symbols[0];
            String quoteAsset = symbols[1];

            BigDecimal totalQuoteAmount = event.getPrice().multiply(event.getQuantity());

            updateWallets(buyerOrder.getUser().getId(), sellerOrder.getUser().getId(),
                    baseAsset, quoteAsset, event.getQuantity(), totalQuoteAmount);

            updateOrderStatus(buyerOrder, event.getQuantity());
            updateOrderStatus(sellerOrder, event.getQuantity());

            log.info("İşlem başarıyla veritabanına işlendi.");

            String destination = "/topic/trades";

            TradeResponse response = new TradeResponse(
                    event.getPrice(),
                    event.getQuantity(),
                    System.currentTimeMillis(),
                    takerSide
            );

            messagingTemplate.convertAndSend(destination, response);
            log.info("İŞLEM REACT'E GÖNDERİLDİ! Adres: {}, Yön: {}", destination, takerSide);

        } catch (Exception e) {
            log.error("İşlem kapatılırken (Settlement) hata oluştu. ", e);
        }

    }

    public void updateWallets(Long buyerId, Long sellerId, String baseAsset, String quoteAsset, BigDecimal baseQuantity, BigDecimal quoteQuantity) {
        // BUYER OPERATİON
        // 1. Deduct the buyer's locked USDT.
        Wallet buyerQuoteWallet = walletRepository.findByUserIdAndAssetSymbol(buyerId, quoteAsset).orElseThrow();
        buyerQuoteWallet.setLockedBalance(buyerQuoteWallet.getLockedBalance().subtract(quoteQuantity));

        // 2. Increase the buyer's available BTC.
        Wallet buyerBaseWallet = walletRepository.findByUserIdAndAssetSymbol(buyerId, baseAsset).orElseThrow();
        buyerBaseWallet.setAvailableBalance(buyerBaseWallet.getAvailableBalance().add(baseQuantity));

        // SELLER OPERATION ---
        // 1. Deduct the seller's locked BTC.
        Wallet sellerBaseWallet = walletRepository.findByUserIdAndAssetSymbol(sellerId, baseAsset).orElseThrow();
        sellerBaseWallet.setLockedBalance(sellerBaseWallet.getLockedBalance().subtract(baseQuantity));

        // 2. Increase the seller's available USDT.
        Wallet sellerQuoteWallet = walletRepository.findByUserIdAndAssetSymbol(sellerId, quoteAsset).orElseThrow();
        sellerQuoteWallet.setAvailableBalance(sellerQuoteWallet.getAvailableBalance().add(quoteQuantity));

        walletRepository.save(buyerQuoteWallet);
        walletRepository.save(buyerBaseWallet);
        walletRepository.save(sellerBaseWallet);
        walletRepository.save(sellerQuoteWallet);
    }

    private void updateOrderStatus(TradeOrder order, BigDecimal executedQuantity) {
        BigDecimal newFilledQuantity = order.getFilledQuantity().add(executedQuantity);
        order.setFilledQuantity(newFilledQuantity);

        if (newFilledQuantity.compareTo(order.getQuantity()) >=0) {
            order.setStatus(TradeOrder.OrderStatus.FILLED);
        } else {
            order.setStatus(TradeOrder.OrderStatus.PARTIAL_FILLED);
        }
        orderRepository.save(order);

    }

}
