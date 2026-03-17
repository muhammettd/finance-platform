package com.muhammetd.finance_platform.dto.request;

import java.math.BigDecimal;

public class TradeOrderRequest {

    private Long userId;
    private String symbolPair; // Örn: "BTC_USDT"
    private String side; // "BUY" or "SELL"
    private String type; // "LIMIT" or "MARKET"
    private BigDecimal price;
    private BigDecimal quantity;

    public TradeOrderRequest(Long userId, String symbolPair, String side, String type, BigDecimal price, BigDecimal quantity) {
        this.userId = userId;
        this.symbolPair = symbolPair;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSymbolPair() {
        return symbolPair;
    }

    public void setSymbolPair(String symbolPair) {
        this.symbolPair = symbolPair;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
