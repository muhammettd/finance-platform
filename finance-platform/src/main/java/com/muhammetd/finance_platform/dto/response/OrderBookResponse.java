package com.muhammetd.finance_platform.dto.response;

import com.muhammetd.finance_platform.dto.request.OrderBookEntry;

import java.util.List;

public class OrderBookResponse {

    private List<OrderBookEntry> bids;
    private List<OrderBookEntry> asks;

    public OrderBookResponse() {

    }

    public OrderBookResponse(List<OrderBookEntry> bids, List<OrderBookEntry> asks) {
        this.bids = bids;
        this.asks = asks;
    }

    public List<OrderBookEntry> getBids() {
        return bids;
    }

    public void setBids(List<OrderBookEntry> bids) {
        this.bids = bids;
    }

    public List<OrderBookEntry> getAsks() {
        return asks;
    }

    public void setAsks(List<OrderBookEntry> asks) {
        this.asks = asks;
    }
}
