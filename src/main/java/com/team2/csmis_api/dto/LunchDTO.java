package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LunchDTO {
    private Integer id;
    private String menu;
    private String price;
    private String companyRate;
    private LocalDate date;
    private Integer adminId; // Assuming User ID is needed
    private Integer restaurantId; // Assuming Restaurant ID is needed

    // Getters and Setters
}
