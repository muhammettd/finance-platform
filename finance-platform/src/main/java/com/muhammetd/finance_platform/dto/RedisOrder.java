package com.muhammetd.finance_platform.dto;

import java.math.BigDecimal;

public class RedisOrder {

    private Long orderId;
    private Long userId;
    private BigDecimal price;
    private BigDecimal quantity;

    public RedisOrder() {

    }

    public RedisOrder(Long orderId, Long userId, BigDecimal price, BigDecimal quantity) {
        this.orderId = orderId;
        this.userId = userId;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
