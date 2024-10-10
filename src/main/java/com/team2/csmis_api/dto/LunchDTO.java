package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LunchDTO {
    private Integer id;
    private List<String> menu; // Updated to match the List<String> type
    private String price;
    private String companyRate;
    private LocalDate date;
    private Integer adminId; // Assuming User ID is needed
    private Integer restaurantId; // Assuming Restaurant ID is needed

    // Getters and Setters are provided by @Data annotation
}
