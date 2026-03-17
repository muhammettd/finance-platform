package com.muhammetd.finance_platform.engine;

import com.muhammetd.finance_platform.dto.RedisOrder;
import com.muhammetd.finance_platform.dto.TradeEvent;
import com.muhammetd.finance_platform.dto.request.OrderBookEntry;
import com.muhammetd.finance_platform.dto.response.OrderBookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MatchingEngine {

    private static final Logger log = LoggerFactory.getLogger(MatchingEngine.class);
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TRADE_TOPIC = "trades.topic";

    public MatchingEngine(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate,
                          SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.messagingTemplate = messagingTemplate;
    }


    private final String SYMBOL_PAIR = "BTC_USDT";

    @Scheduled(fixedDelay = 5000)
    public void matchOrders() {

        System.out.println(" [MOTOR] Eşleştirme motoru uyandı ve kontrol yapıyor...");

        // We are manually fixing the variables for now to avoid errors:
        String bidsKey = "ORDER_BOOK:BTC_USDT:BIDS";
        String asksKey = "ORDER_BOOK:BTC_USDT:ASKS";

        try {
            Set<ZSetOperations.TypedTuple<String>> highestBids = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(bidsKey, 0 , 0);

            Set<ZSetOperations.TypedTuple<String>> lowestAsks = redisTemplate.opsForZSet()
                    .rangeWithScores(asksKey, 0, 0);

            // 2. REDIS'TE VERİ BULDU MU KONTROLÜ
            int aliciSayisi = (highestBids != null) ? highestBids.size() : 0;
            int saticiSayisi = (lowestAsks != null) ? lowestAsks.size() : 0;
            System.out.println("[MOTOR] Bulunan Alıcı: " + aliciSayisi + " | Bulunan Satıcı: " + saticiSayisi);

            if (highestBids == null || highestBids.isEmpty() || lowestAsks == null || lowestAsks.isEmpty()) {
                return;
            }

            String topBidJson = highestBids.iterator().next().getValue();
            String topAskJson = lowestAsks.iterator().next().getValue();

            RedisOrder bidOrder = objectMapper.readValue(topBidJson, RedisOrder.class);
            RedisOrder askOrder = objectMapper.readValue(topAskJson, RedisOrder.class);

            if (bidOrder.getPrice().compareTo(askOrder.getPrice()) >= 0) {
                System.out.println(" [MOTOR] EŞLEŞME BULUNDU! Fiyatlar uyuyor!");


                BigDecimal tradeQuantity = bidOrder.getQuantity().min(askOrder.getQuantity());
                BigDecimal executePrice = askOrder.getPrice();

                BigDecimal remainingBidQty = bidOrder.getQuantity().subtract(tradeQuantity);
                BigDecimal remainingAskQty = askOrder.getQuantity().subtract(tradeQuantity);

                updateOrderBook(bidsKey, topBidJson, bidOrder, remainingBidQty);
                updateOrderBook(asksKey, topAskJson, askOrder, remainingAskQty);

                processTrade(bidOrder.getOrderId(), askOrder.getOrderId(), executePrice, tradeQuantity, "BTC_USDT");
                broadcastOrderBook("BTC_USDT");
            }

        } catch (Exception e) {
            System.err.println(" [MOTOR] Hata patladı: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastOrderBook(String symbolPair) {
        String bidsKey = "ORDER_BOOK:" + symbolPair + ":BIDS";
        String asksKey = "ORDER_BOOK:" + symbolPair + ":ASKS";

        try {
            // En iyi 15 Alıcıyı Çek (Yüksek fiyattan düşüğe - reverseRange)
            Set<String> bidsJson = redisTemplate.opsForZSet().reverseRange(bidsKey, 0, 14);
            List<OrderBookEntry> bids = new ArrayList<>();
            if (bidsJson != null) {
                for (String json : bidsJson) {
                    RedisOrder order = objectMapper.readValue(json, RedisOrder.class);
                    bids.add(new OrderBookEntry(order.getPrice(), order.getQuantity()));
                }
            }

            // En ucuz 15 Satıcıyı Çek (Düşük fiyattan yükseğe - range)
            Set<String> asksJson = redisTemplate.opsForZSet().range(asksKey, 0, 14);
            List<OrderBookEntry> asks = new ArrayList<>();
            if (asksJson != null) {
                for (String json : asksJson) {
                    RedisOrder order = objectMapper.readValue(json, RedisOrder.class);
                    asks.add(new OrderBookEntry(order.getPrice(), order.getQuantity()));
                }
            }

            // (DTO) Hazırla ve React'e Gönder
            OrderBookResponse response = new OrderBookResponse(bids, asks);
            messagingTemplate.convertAndSend("/topic/orderbook" + symbolPair, response);

            System.out.println("[YAYIN] Emir Defteri (OrderBook) React'e güncellendi!");

        } catch (Exception e) {
            System.err.println("[YAYIN] Emir defteri gönderilirken hata: " + e.getMessage());
        }
    }

    private void updateOrderBook(String redisKey, String oldJson, RedisOrder order, BigDecimal remainingQty) throws Exception {
        redisTemplate.opsForZSet().remove(redisKey, oldJson);

        if (remainingQty.compareTo(BigDecimal.ZERO) > 0) {
            order.setQuantity(remainingQty);
            String newJson = objectMapper.writeValueAsString(order);
            redisTemplate.opsForZSet().add(redisKey, newJson, order.getPrice().doubleValue());
            log.info("Emir güncellendi (Kalan miktar: {}: ID {}", remainingQty, order.getOrderId());

        } else {
            log.info("Emir tamamen gerçekleşti ve kuyruktan silindi: ID {]", order.getOrderId());
        }
    }

    private void processTrade(Long buyerOrderId, Long sellerOrderId, BigDecimal price, BigDecimal quantity, String symbolPair) {
        try {
            System.out.println("TRADE GERÇEKLEŞTİ: " + price + " Fiyatından " + quantity + " miktar.");

            TradeEvent event = new TradeEvent(buyerOrderId, sellerOrderId, price, quantity, symbolPair);

            String eventJson = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("trades.topic", symbolPair, eventJson);

            System.out.println(" Eşleşme Kafka'ya başarıyla gönderildi: " + eventJson);
        } catch (Exception e) {
            System.err.println("KAFKA GÖNDERİM HATASI: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
