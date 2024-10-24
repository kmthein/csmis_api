package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LunchDTO {
    private Integer id;
    private String[] menu;
    private double price;
    private double companyRate;
    private LocalDate date;
    private Integer adminId;
    private Integer restaurantId;
}
