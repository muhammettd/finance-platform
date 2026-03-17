package com.muhammetd.finance_platform.controller;

import com.muhammetd.finance_platform.dto.request.TradeOrderRequest;
import com.muhammetd.finance_platform.dto.response.OrderBookResponse;
import com.muhammetd.finance_platform.dto.response.TradeResponse;
import com.muhammetd.finance_platform.model.TradeOrder;
import com.muhammetd.finance_platform.service.OrderBookService;
import com.muhammetd.finance_platform.service.TradeOrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "*")
public class TradeOrderController {

    private final TradeOrderService tradeOrderService;
    private final OrderBookService orderBookService;
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public TradeOrderController(TradeOrderService tradeOrderService, OrderBookService orderBookService, RedisTemplate redisTemplate,
                                ObjectMapper objectMapper) {
        this.tradeOrderService = tradeOrderService;
        this.orderBookService = orderBookService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody TradeOrderRequest request) {
        try {
            TradeOrder createdOrder = tradeOrderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body("The operation could not be performed: " + e.getMessage());
        }
    }

    @GetMapping("/{symbolPair}")
    public ResponseEntity<OrderBookResponse> getOrderBook(@PathVariable String symbolPair) {
        OrderBookResponse response = orderBookService.getOrderBookSnapshot(symbolPair);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{symbolPair}")
    public ResponseEntity<List<TradeResponse>> getTradeHistory(@PathVariable String symbolPair) {
        List<TradeResponse> history = orderBookService.getFilledOrdersHistory(symbolPair);
        return ResponseEntity.ok(history);
    }


}

