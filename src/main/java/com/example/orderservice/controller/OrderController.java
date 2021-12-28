package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
public class OrderController {

    private Environment env;
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    public OrderController(Environment env, OrderService orderService) {
        this.env = env;
        this.orderService = orderService;
    }

    @GetMapping(value = "/health_check")
    public String status() {
        return String.format("It's working in catalog service on port %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId) {
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();
        for (OrderEntity orderEntity : orderList) {
            result.add(objectMapper.convertValue(orderEntity, ResponseOrder.class));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@RequestBody RequestOrder order, @PathVariable("userId") String userId) {

        OrderDto orderDto = objectMapper.convertValue(order, OrderDto.class);
        orderDto.setUserId(userId);
        orderService.createOrder(orderDto);
        ResponseOrder responseOrder = objectMapper.convertValue(orderDto, ResponseOrder.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

}
