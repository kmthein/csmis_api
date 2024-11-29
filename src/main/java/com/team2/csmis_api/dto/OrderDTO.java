package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private LocalDate orderDate;
    private String message;
    private Integer restaurantId; // Restaurant foreign key
    private String restaurantName;
    private Integer adminId; // Admin User foreign key
    private List<OrderRowDTO> rows;

    public OrderDTO(Integer id, LocalDate orderDate, String message, Integer restaurantId, Integer adminId) {
        this.id = id;
        this.orderDate = orderDate;
        this.message = message;
        this.restaurantId = restaurantId;
        this.adminId = adminId;
    }

    public OrderDTO(Integer id, LocalDate orderDate, String message, Integer restaurantId, String restaurantName, Integer adminId) {
        this.id = id;
        this.orderDate = orderDate;
        this.message = message;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.adminId = adminId;
    }
}