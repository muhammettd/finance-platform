package com.muhammetd.finance_platform.repository;

import com.muhammetd.finance_platform.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {

    // Retrieves the user's entire order history.
    List<TradeOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    // It only retrieves the user's open (not yet executed) orders.
    List<TradeOrder> findByUserIdAndStatus(Long userId, TradeOrder.OrderStatus status);

    // Retrieve the last 50 orders with the status FILLED from the database, sorting them with the newest ones at the top.
    List<TradeOrder> findTop50BySymbolPairAndStatusOrderByIdDesc(String symbolPair, TradeOrder.OrderStatus status);

}
