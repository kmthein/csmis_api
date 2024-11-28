package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.OrderDTO;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;


    @GetMapping("/quantity")
    public long getQuantity(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return orderService.getQuantity(date);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getOrderQuantity")
    public long getOrderQuantity(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return orderService.getOrderQuantity(date);
    }
//    @GetMapping("/restaurant")
//    public ResponseEntity<String> getRestaurantByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
//        Optional<Restaurant> restaurant = orderService.getRestaurantByOrderDate(date);
//
//        if (restaurant.isPresent()) {
//            return ResponseEntity.ok(restaurant.get().getName());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No restaurant found for the given date.");
//        }
//    }
    @GetMapping("/restaurant")
    public ResponseEntity<List<Restaurant>> getRestaurantByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Restaurant> restaurants = orderService.getRestaurantsByOrderDate(date);

        if (!restaurants.isEmpty()) {
            return ResponseEntity.ok(restaurants);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }
}