package com.muhammetd.finance_platform.dto.response;

import java.math.BigDecimal;

public class TradeResponse {

    private BigDecimal price;

    private BigDecimal quantity;

    private long timestamp;

    private String side;

    public TradeResponse() {
    }

    public TradeResponse(BigDecimal price, BigDecimal quantity, long timestamp, String side) {
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.side = side;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}
