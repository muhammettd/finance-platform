package com.muhammetd.finance_platform.service;

import com.muhammetd.finance_platform.dto.request.TradeOrderRequest;
import com.muhammetd.finance_platform.model.TradeOrder;
import com.muhammetd.finance_platform.model.User;
import com.muhammetd.finance_platform.repository.TradeOrderRepository;
import com.muhammetd.finance_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TradeOrderService {

    private final TradeOrderRepository tradeOrderRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final OrderBookService orderBookService;

    public TradeOrderService(TradeOrderRepository tradeOrderRepository, UserRepository userRepository, WalletService walletService, OrderBookService orderBookService) {
        this.tradeOrderRepository = tradeOrderRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.orderBookService = orderBookService;
    }

    @Transactional
    public TradeOrder createOrder(TradeOrderRequest request) {

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0 ||
                request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Fiyat ve miktar 0'dan büyük olmalı");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Separating the Trading Pair (e.g., "BTC_USDT" -> Base: BTC, Quote: USDT)
        String[] symbols = request.getSymbolPair().split("_");
        if (symbols.length != 2) {
            throw new IllegalArgumentException("Geçersiz sembol formaatı. Örn: BTC_USDT olmalı");
        }

        String baseAsset = symbols[0]; // The actual asset bought/sold (BTC)
        String quoteAsset = symbols[1]; // The asset on which the price is determined (USDT)

        TradeOrder.OrderSide side = TradeOrder.OrderSide.valueOf(request.getSide());
        TradeOrder.OrderType type = TradeOrder.OrderType.valueOf(request.getType());

        // Locked Balance Calculation and Wallet Transaction
        if (side == TradeOrder.OrderSide.BUY) {
            // ALIM yapılıyorsa: Kullanıcı USDT (Quote Asset) harcayacaktır.
            BigDecimal totalCost = request.getPrice().multiply(request.getQuantity());
            walletService.lockBalanceForOrder(user.getId(), quoteAsset, totalCost);

        } else if (side == TradeOrder.OrderSide.SELL) {
            // SATIM yapılıyorsa: Kullanıcı elindeki BTC'yi (Base Asset) satacaktır.
            walletService.lockBalanceForOrder(user.getId(), baseAsset, request.getQuantity());
        }

        TradeOrder order = new TradeOrder();
        order.setUser(user);
        order.setSymbolPair(request.getSymbolPair());
        order.setSide(side);
        order.setType(type);
        order.setPrice(request.getPrice());
        order.setQuantity(request.getQuantity());
        order.setStatus(TradeOrder.OrderStatus.PENDING);
        order.setFilledQuantity(BigDecimal.ZERO);

        TradeOrder savedOrder = tradeOrderRepository.save(order);

        orderBookService.addOrderToBook(savedOrder);

        return savedOrder;

    }

}
