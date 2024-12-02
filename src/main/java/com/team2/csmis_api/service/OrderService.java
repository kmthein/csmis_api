package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.OrderDTO;
import com.team2.csmis_api.dto.OrderRowDTO;
import com.team2.csmis_api.entity.Order;
import com.team2.csmis_api.entity.OrderRow;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.repository.OrderRepository;
import com.team2.csmis_api.repository.OrderRowRepository;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserHasLunchRepository userHasLunchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRowRepository orderRowRepository;

    @Autowired
    private ModelMapper modelMapper;

    public OrderDTO getOrderByRowDate(LocalDate date) {
        System.out.println(date);
        OrderDTO orderDTO = orderRowRepository.findOrderByDate(date);
        if(orderDTO != null) {
            List<OrderRowDTO> orderRowDTOS = orderRowRepository.getOrderRowsByOrderId(orderDTO.getId());
            orderDTO.setRows(orderRowDTOS);
        }
        return orderDTO;
    }

    @Transactional
    public List<OrderRowDTO> getNextWeekOrder() {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate next Monday
        LocalDate nextMonday = today.with(DayOfWeek.MONDAY).isAfter(today) ?
                today.with(DayOfWeek.MONDAY) :
                today.plusWeeks(1).with(DayOfWeek.MONDAY);

        // Calculate next Friday
        LocalDate nextFriday = nextMonday.plusDays(4);

        // Fetch data from repository
        return orderRepository.getOrderRow(nextMonday, nextFriday);
    }

    public long getOrderQuantity(Date date) {
        return orderRowRepository.getOrderRowQuantity(date);
    }

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

        for(OrderRowDTO row : orderDTO.getRows()) {
            System.out.println("Date: " + row.getLunchDate().toString() + ",pax: " + row.getQuantity());
        }

        User admin = userRepository.getUserById(orderDTO.getAdminId());
        if(admin == null) {
            throw new ResourceNotFoundException("Admin not found");
        }
        order.setUser(admin);
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

    public long getOrderQuantity(LocalDate date) {
        return orderRepository.getTotalQuantityByDate(date);
    }


//    public Optional<Restaurant> getRestaurantByOrderDate(LocalDate orderDate) {
//        return orderRepository.findByOrderDate(orderDate)
//                .map(Order::getRestaurant);
//    }

    public List<Restaurant> getRestaurantsByOrderDate(LocalDate selectedDate) {
        return orderRepository.findRestaurantByOrderDate(selectedDate);
    }
}