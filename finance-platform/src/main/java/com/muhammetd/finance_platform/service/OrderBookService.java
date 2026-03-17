package com.muhammetd.finance_platform.service;

import com.muhammetd.finance_platform.dto.RedisOrder;
import com.muhammetd.finance_platform.dto.request.OrderBookEntry;
import com.muhammetd.finance_platform.dto.response.OrderBookResponse;
import com.muhammetd.finance_platform.dto.response.TradeResponse;
import com.muhammetd.finance_platform.model.TradeOrder;
import com.muhammetd.finance_platform.repository.TradeOrderRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderBookService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final TradeOrderRepository tradeOrderRepository;

    public OrderBookService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, SimpMessagingTemplate messagingTemplate, TradeOrderRepository tradeOrderRepository) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
        this.tradeOrderRepository = tradeOrderRepository;
    }

    public void addOrderToBook(TradeOrder order) {
        try {
            RedisOrder redisOrder = new RedisOrder(
                    order.getId(),
                    order.getUser().getId(),
                    order.getPrice(),
                    order.getQuantity()
            );

            String orderJson = objectMapper.writeValueAsString(redisOrder);
            double score = order.getPrice().doubleValue();
            String redisKey = "ORDER_BOOK:" + order.getSymbolPair() + ":";

            if (order.getSide() == TradeOrder.OrderSide.BUY) {
                redisKey += "BIDS";
            } else {
                redisKey += "ASKS";
            }

            redisTemplate.opsForZSet().add(redisKey, orderJson, score);
            System.out.println("Emir Redis Order Book'a eklendi:" + redisKey + " - Fiyat " + score);

            broadcastOrderBook(order.getSymbolPair());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Emir JSON'a dönüştürülemedi.", e);
        }
    }

    public OrderBookResponse getOrderBookSnapshot(String symbolPair) {
        String bidsKey = "ORDER_BOOK:" + symbolPair + ":BIDS";
        String asksKey = "ORDER_BOOK:" + symbolPair + ":ASKS";

        try {
            // Alıcıları (Bids) Çek - Yüksek fiyattan düşüğe
            Set<String> bidsJson = redisTemplate.opsForZSet().reverseRange(bidsKey, 0, 14);
            List<OrderBookEntry> bids = new ArrayList<>();
            if (bidsJson != null) {
                for (String json : bidsJson) {
                    RedisOrder order = objectMapper.readValue(json, RedisOrder.class);
                    bids.add(new OrderBookEntry(order.getPrice(), order.getQuantity()));
                }
            }

            // Satıcıları (Asks) Çek - Düşük fiyattan yükseğe
            Set<String> asksJson = redisTemplate.opsForZSet().range(asksKey, 0, 14);
            List<OrderBookEntry> asks = new ArrayList<>();
            if (asksJson != null) {
                for (String json : asksJson) {
                    RedisOrder order = objectMapper.readValue(json, RedisOrder.class);
                    asks.add(new OrderBookEntry(order.getPrice(), order.getQuantity()));
                }
            }

            return new OrderBookResponse(bids, asks);

        } catch (Exception e) {
            System.err.println("OrderBook Snapshot çekilirken hata: " + e.getMessage());
            return new OrderBookResponse(new ArrayList<>(), new ArrayList<>());
        }
    }

    public List<TradeResponse> getFilledOrdersHistory(String symbolPair) {
        List<TradeOrder> filledOrders = tradeOrderRepository.findTop50BySymbolPairAndStatusOrderByIdDesc(
                symbolPair,
                TradeOrder.OrderStatus.FILLED
        );

        List<TradeResponse> historyList = new ArrayList<>();

        for (TradeOrder order : filledOrders) {

            long timestamp = System.currentTimeMillis();

            TradeResponse response = new TradeResponse(
                    order.getPrice(),
                    order.getFilledQuantity(),
                    timestamp,
                    order.getSide().name()
            );
            historyList.add(response);
        }

        return historyList;
    }

    private void broadcastOrderBook(String symbolPair) {
        try {
            String bidsKey = "ORDER_BOOK:" + symbolPair + ":BIDS";
            String asksKey = "ORDER_BOOK:" + symbolPair + ":ASKS";

            Set<String> bidsJson = redisTemplate.opsForZSet().reverseRange(bidsKey, 0, 14);
            List<OrderBookEntry> bids = new ArrayList<>();
            if (bidsJson != null) {
                for (String json : bidsJson) {
                    RedisOrder order = objectMapper.readValue(json, RedisOrder.class);
                    bids.add(new OrderBookEntry(order.getPrice(), order.getQuantity()));
                }
            }

            Set<String> asksJson = redisTemplate.opsForZSet().range(asksKey, 0, 14);
            List<OrderBookEntry> asks = new ArrayList<>();
            if (asksJson != null) {
                for (String json : asksJson) {
                    RedisOrder order = objectMapper.readValue(json, RedisOrder.class);
                    asks.add(new OrderBookEntry(order.getPrice(), order.getQuantity()));
                }
            }

            OrderBookResponse response = new OrderBookResponse(bids, asks);
            messagingTemplate.convertAndSend("/topic/orderbook/" + symbolPair, response);
            System.out.println("Canlı yayın yapıldı: /topic/orderbook/" + symbolPair);

        } catch (Exception e) {
            System.err.println("Canlı yayın (Broadcast) sırasında hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

