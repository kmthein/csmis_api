package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {
    private Integer id;
    private LocalDate orderDate;
    private String message;
    private Integer restaurantId; // Restaurant foreign key
    private Integer adminId; // Admin User foreign key
    private List<OrderRowDTO> rows;
}