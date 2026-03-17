package com.muhammetd.finance_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_order")
public class TradeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String symbolPair;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type;

    @Column(precision = 24, scale = 8, nullable = false)
    private BigDecimal price;

    @Column(precision = 24, scale = 8, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 24, scale = 8, nullable = false)
    private BigDecimal filledQuantity = BigDecimal.ZERO; // If the order is executed in parts (e.g., You requested 10 BTC, 4 were received)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum OrderSide { BUY, SELL }
    public enum OrderType { LIMIT, MARKET }
    public enum OrderStatus { PENDING, PARTIAL_FILLED, FILLED, CANCELED }

    public TradeOrder() {
    }

    public TradeOrder(Long id, User user, String symbolPair, OrderType type,
                      LocalDateTime updatedAt, LocalDateTime createdAt, OrderStatus status,
                      BigDecimal filledQuantity, BigDecimal quantity, BigDecimal price, OrderSide side) {
        this.id = id;
        this.user = user;
        this.symbolPair = symbolPair;
        this.type = type;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.status = status;
        this.filledQuantity = filledQuantity;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
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

    public String getSymbolPair() {
        return symbolPair;
    }

    public void setSymbolPair(String symbolPair) {
        this.symbolPair = symbolPair;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(BigDecimal filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
