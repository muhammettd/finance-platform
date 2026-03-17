package com.muhammetd.finance_platform.dto;

import java.math.BigDecimal;

public class TradeEvent {

    private Long buyerOrderId;
    private Long sellerOrderId;
    private BigDecimal price;
    private BigDecimal quantity;
    private String symbolPair;

    public TradeEvent() {
    }

    public TradeEvent(Long buyerOrderId, Long sellerOrderId, BigDecimal price, BigDecimal quantity, String symbolPair) {
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
        this.price = price;
        this.quantity = quantity;
        this.symbolPair = symbolPair;
    }

    public Long getBuyerOrderId() {
        return buyerOrderId;
    }

    public void setBuyerOrderId(Long buyerOrderId) {
        this.buyerOrderId = buyerOrderId;
    }

    public Long getSellerOrderId() {
        return sellerOrderId;
    }

    public void setSellerOrderId(Long sellerOrderId) {
        this.sellerOrderId = sellerOrderId;
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

    public String getSymbolPair() {
        return symbolPair;
    }

    public void setSymbolPair(String symbolPair) {
        this.symbolPair = symbolPair;
    }
}
