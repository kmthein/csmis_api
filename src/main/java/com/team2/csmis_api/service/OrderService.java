package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.OrderDTO;
import com.team2.csmis_api.dto.OrderRowDTO;
import com.team2.csmis_api.entity.Order;
import com.team2.csmis_api.entity.OrderRow;
import com.team2.csmis_api.repository.OrderRepository;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserHasLunchRepository userHasLunchRepository;

    @Autowired
    private ModelMapper modelMapper;

    public long getQuantity(Date date) {
        return userHasLunchRepository.countByDate(date);
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {

        System.out.println("Order dtooo: " + orderDTO);
        Order order = modelMapper.map(orderDTO, Order.class);

        // Set reference for OrderRows
        if (order.getRows() != null) {
            order.getRows().forEach(row -> row.setOrder(order));
        }

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    public OrderDTO getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found for ID: " + id));
        return modelMapper.map(order, OrderDTO.class);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrder(Integer id, OrderDTO updatedOrderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found for ID: " + id));

        // Update fields
        existingOrder.setOrderDate(updatedOrderDTO.getOrderDate());
        existingOrder.setMessage(updatedOrderDTO.getMessage());
        existingOrder.setRestaurant(modelMapper.map(updatedOrderDTO.getRestaurantId(), Order.class).getRestaurant());
        existingOrder.setUser(modelMapper.map(updatedOrderDTO.getAdminId(), Order.class).getUser());

        // Clear and add new rows
        existingOrder.getRows().clear();
        if (updatedOrderDTO.getRows() != null) {
            updatedOrderDTO.getRows().forEach(rowDTO -> {
                OrderRow row = modelMapper.map(rowDTO, OrderRow.class);
                row.setOrder(existingOrder);
                existingOrder.getRows().add(row);
            });
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }
}